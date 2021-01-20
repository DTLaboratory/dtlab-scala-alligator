package dtlaboratory.dtlab.actors

import akka.persistence._
import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.Conf.webhooks
import dtlaboratory.dtlab.models._
import dtlaboratory.dtlab.observe.Observer
import dtlaboratory.dtlab.operators._

// ejs todo: evaluate appropriate exec context
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}

object DtActor extends LazyLogging {
  def name: String = this.getClass.getName
}

class DtActor extends DtPersistentActorBase[DtState, Telemetry] {

  override var state: DtState = DtState()

  /*  temp state so that each operator can see old state
   *  and only apply all changes once at end
   */
  private def applyOperators(t: Telemetry): Unit = {

    var newState = state

    def applyOperatorResult(t: Telemetry): Unit =
      newState = DtState(newState.state + (t.idx -> t))

    // find operators that list this t's index as input and apply them
    val ops = operators.operators.values.filter(_.input.contains(t.idx))
    ops.foreach(op => {
      ApplySimpleBuiltInOperator(t, state, op).foreach(r =>
        applyOperatorResult(r))
    })
    ops.foreach(op => {
      ApplyComplexBuiltInOperator(t, state, op).foreach(r =>
        applyOperatorResult(r))
    })

    state = newState

  }

  private def handleTelemetry(t: Telemetry, recover: Boolean = false): Unit = {

    val oldState = state.state.values.map(_.value)

    applyOperators(t) // apply operators to effects that come from outside the DT
    state = DtState(state.state + (t.idx -> t))
    val sndr = sender()
    if (!recover)
      persistAsync(t) { _ =>
        takeSnapshot()
        if (sndr != self) {
          if (!oldState.equals(state.state.values.map(_.value))) {
            logger.debug(s"state change")
            webhooks ! StateChange()
          } else {
            logger.debug(s"no state change")
          }
          sndr ! DtOk()
        }
      }
  }

  private def handleGetJrnl(m: GetJrnl): Unit = {
    val result = grabJrnl(m.limit, m.offset)
    val sndr = sender()
    result onComplete {
      case Success(r) =>
        sndr ! r
      case Failure(exception) =>
        sndr ! DtErr(exception.getMessage)
    }
  }

  override def receiveCommand: Receive = {

    case _: TakeSnapshot => takeSnapshot(true)

    case m: DtMsg[Any @unchecked]
        if m.path().trail.nonEmpty && !m
          .path()
          .trail
          .exists(leaf =>
            leaf.typeId.name == self.path.name && leaf.instanceId.name == "children" && leaf.trail.isEmpty) =>
      logger.debug(s"${self.path.name} forwarding $m")
      upsert(m)

    case m: GetJrnl => handleGetJrnl(m)

    case _: GetOperators => sender() ! operators

    case _: GetChildrenNames => sender ! children

    case _: GetActorInfo =>
      sender ! ActorInfo(persistenceId, self)

    case _: DeleteOperators =>
      operators = OperatorMap()
      takeSnapshot(true)
      sender ! DtOk()

    case op: OperatorMsg =>
      operators = OperatorMap(operators.operators + (op.c.name -> op.c))
      takeSnapshot(true)
      sender ! operators

    // telemetry msgs are sent with dtpath addressing wrappers and
    // are the entry point / triggers for all complex and simple state change processing
    case tm: TelemetryMsg => handleTelemetry(tm.c)

    case _: GetState => sender ! state

    case _: SaveSnapshotSuccess =>
      logger.debug("saved snapshot")
    case m =>
      logger.warn(s"unexpected message: $m")

  }

  override def receiveRecover: Receive = {

    case t: Telemetry =>
      handleTelemetry(t, recover = true)

    case SnapshotOffer(_, snapshot: DtStateHolder[DtState] @unchecked) =>
      Try {
        // if there is a change in class implementation you will fail here
        state = snapshot.state
        children = snapshot.children
        operators = snapshot.operators
      } match {
        case Success(_) =>
          Observer("recovered_dt_actor_state_from_snapshot")
        case Failure(e) =>
          Observer("recovery_of_dt_actor_state_from_snapshot_failed")
          logger.error(s"can not recover state: $e", e)
      }

    case _: RecoveryCompleted =>
      logger.debug(
        s"${self.path}: Recovery completed. State: $state Children: $children")
      Observer("resurrected_dt_actor")

      if (state.state.isEmpty && children.children.isEmpty)
        webhooks ! Creation()

    case x =>
      Observer("resurrected_dt_actor_unexpected_msg")
      logger.warn(s"unexpected recover msg: $x")

  }

}
