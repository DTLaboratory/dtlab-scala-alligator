package somind.dtlab.actors

import akka.persistence._
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models._
import somind.dtlab.observe.Observer

object DtActor extends LazyLogging {
  def name: String = this.getClass.getName
}

class DtActor extends DtPersistentActorBase[DtState] {

  override var state: DtState = DtState()

  override def receiveCommand: Receive = {

    case m: DtMsg[Any @unchecked] if m.path().trail.nonEmpty =>
      upsert(m.trailMsg())

    case tm: TelemetryMsg =>
      state = DtState(state.state + (tm.c.idx -> tm.c))
      persistAsync(tm.c) { _ =>
        sender ! DtOk()
        takeSnapshot()
      }

    case _: DtGetState =>
      sender ! state

    case _: SaveSnapshotSuccess =>
    case m =>
      logger.warn(s"unexpected message: $m")
      sender ! None

  }

  override def receiveRecover: Receive = {

    case t: Telemetry =>
      state = DtState(state.state + (t.idx -> t))

    case SnapshotOffer(_, s: DtState @unchecked) =>
      state = s
      Observer("recovered_dt_actor_state_from_snapshot")

    case _: RecoveryCompleted =>
      logger.debug(s"${self.path}: Recovery completed. State: $state")
      Observer("resurrected_dt_actor")

    case x =>
      logger.warn(s"unexpected recover msg: $x")

  }

}
