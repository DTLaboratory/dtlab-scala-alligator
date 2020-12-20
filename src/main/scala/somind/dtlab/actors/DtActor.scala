package somind.dtlab.actors

import akka.persistence._
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models._
import somind.dtlab.observe.Observer
import somind.dtlab.operators.ApplyBuiltInOperator

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object DtActor extends LazyLogging {
  def name: String = this.getClass.getName
}

class DtActor extends DtPersistentActorBase[DtState, Telemetry] {

  override var state: DtState = DtState()

  def applyOperator(t: Telemetry): Unit = {
    // find operators that list this t's index as input and apply them
    val ops = operators.operators.values.filter(_.input.contains(t.idx))
    ops.foreach(op => {
      ApplyBuiltInOperator(t, state, op, (t: Telemetry) => { self ! t })
    })
  }

  override def receiveCommand: Receive = {

    case _: TakeSnapshot =>
      takeSnapshot(true)

    case m: DtMsg[Any @unchecked]
        if m.path().trail.nonEmpty && !m
          .path()
          .trail
          .exists(leaf =>
            leaf.typeId == self.path.name && leaf.instanceId == "children" && leaf.trail.isEmpty) =>
      logger.debug(s"${self.path.name} forwarding $m")
      upsert(m)

    case m: GetJrnl =>
      val result = grabJrnl(m.limit, m.offset)
      val sndr = sender()
      result onComplete {
        case Success(r) =>
          sndr ! r
        case Failure(exception) =>
          sndr ! DtErr(exception.getMessage)
      }

    case _: GetOperators =>
      sender() ! operators

    case _: GetChildrenNames =>
      sender ! children

    case _: DeleteOperators =>
      operators = OperatorMap()
      takeSnapshot(true)
      sender ! DtOk()

    case op: OperatorMsg =>
      operators = OperatorMap(operators.operators + (op.c.name -> op.c))
      takeSnapshot(true)
      sender ! operators

    // raw telemetry are derived inside the operators and sent from self
    case t: Telemetry =>
      state = DtState(state.state + (t.idx -> t))
      val sndr = sender()
      persistAsync(t) { _ =>
        takeSnapshot()
      }

    // telemetry msgs are sent with dtpath addressing wrappers and
    // are the entry point / triggers for all complex and simple state change processing
    case tm: TelemetryMsg =>
      state = DtState(state.state + (tm.c.idx -> tm.c))
      val sndr = sender()
      persistAsync(tm.c) { _ =>
        takeSnapshot()
        if (sndr != self) {
          applyOperator(tm.c) // apply operators to effects that come from outside the DT
          sender ! DtOk()
        }
      }

    case _: GetState =>
      sender ! state

    case _: SaveSnapshotSuccess =>
    case m =>
      logger.warn(s"unexpected message: $m")
      sender ! None

  }

  override def receiveRecover: Receive = {

    case t: Telemetry =>
      state = DtState(state.state + (t.idx -> t))

    case SnapshotOffer(_, snapshot: DtStateHolder[DtState] @unchecked) =>
      state = snapshot.state
      children = snapshot.children
      operators = snapshot.operators
      Observer("recovered_dt_actor_state_from_snapshot")

    case _: RecoveryCompleted =>
      logger.debug(
        s"${self.path}: Recovery completed. State: $state Children: $children")
      Observer("resurrected_dt_actor")

    case x =>
      logger.warn(s"unexpected recover msg: $x")

  }

}
