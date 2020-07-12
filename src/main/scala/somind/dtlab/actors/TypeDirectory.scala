package somind.dtlab.actors

import akka.persistence._
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models._
import somind.dtlab.observe.Observer

object TypeDirectory extends LazyLogging {
  def name: String = this.getClass.getName
}

class TypeDirectory extends DtLabActor[DtTypeMap] {

  override var state: DtTypeMap = DtTypeMap(types = Map())

  override def receiveCommand: Receive = {

    case dt: DtType =>
      state.types.get(dt.name) match {
        case Some(prev) =>
          sender ! Some(prev)
        case _ =>
          state = DtTypeMap( state.types + (dt.name -> dt) )
          sender ! Some(dt)
          persistAsync(dt) { _ =>
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
      Observer("reapplied_actor_command_from_jrnl")
      state = DtTypeMap( state.types + (dt.name -> dt) )

    case SnapshotOffer(_, s: DtTypeMap @unchecked) =>
      Observer("recovered_actor_state_from_snapshot")
      state = s

    case _: RecoveryCompleted =>
      Observer("resurrected_actor")
      logger.debug(s"${self.path}: Recovery completed. State: $state")

    case x =>
      logger.warn(s"unexpected recover msg: $x")

  }

}
