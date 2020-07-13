package somind.dtlab.actors

import akka.persistence._
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models._
import somind.dtlab.observe.Observer

object DtDirectory extends LazyLogging {
  def name: String = this.getClass.getName
}

class DtDirectory extends DtPersistentActorBase[DtTypeMap] {

  override var state: DtTypeMap = DtTypeMap(types = Map())

  def isValidTelemetry(m: TelemetryMsg): DtResult = {
    val dtType = m.path().typeName()
    state.types.get(dtType) match {
      case Some(t) =>
        if (m.c.idx >= t.props.length || m.c.idx < 0)
          DtErr("idx out of bounds")
        else
          DtOk()
      case _ =>
        DtErr("type not defined")
    }
  }

  def isValid(m: DtMsg[Any]): DtResult = {
    val dtType = m.path().typeName()
    state.types.get(dtType) match {
      case Some(_) =>
        DtOk()
      case _ =>
        DtErr("type not defined")
    }
  }

  override def receiveCommand: Receive = {

    case m: TelemetryMsg if m.path().trail.nonEmpty =>
      isValidTelemetry(m) match {
        case DtOk() =>
          upsert(m.trailMsg())
        case e =>
          sender ! e
      }

    case m: DtMsg[Any @unchecked] if m.path().trail.nonEmpty =>
      isValid(m) match {
        case DtOk() =>
          upsert(m.trailMsg())
        case e =>
          sender ! e
      }

    case dt: DtType =>
      state.types.get(dt.name) match {
        case Some(prev) =>
          sender ! Some(prev)
        case _ =>
          state = DtTypeMap(state.types + (dt.name -> dt))
          persistAsync(dt) { _ =>
            sender ! Some(dt)
            takeSnapshot()
          }
      }

    case typeName: String =>
      state.types.get(typeName) match {
        case Some(dt) =>
          sender ! Some(dt)
        case _ =>
          sender ! None
      }

    case _: SaveSnapshotSuccess =>
    case m =>
      logger.warn(s"unexpected message: $m")
      sender ! None

  }

  override def receiveRecover: Receive = {

    case dt: DtType =>
      state = DtTypeMap(state.types + (dt.name -> dt))
      Observer("reapplied_type_actor_command_from_jrnl")

    case SnapshotOffer(_, s: DtTypeMap @unchecked) =>
      Observer("recovered_type_actor_state_from_snapshot")
      state = s

    case _: RecoveryCompleted =>
      Observer("resurrected_type_actor")
      logger.debug(s"${self.path}: Recovery completed. State: $state")

    case x =>
      logger.warn(s"unexpected recover msg: $x")

  }

}
