package somind.dtlab.actors

import akka.persistence.{RecoveryCompleted, SnapshotOffer}
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models.DtType
import somind.dtlab.observe.Observer

object TypeDirectory extends LazyLogging {
  def name: String = this.getClass.getName
}

class TypeDirectory extends DtLabActor[Map[String, DtType]] {

  override var state: Map[String, DtType] = Map()

  override def receiveCommand: Receive = {

    case dt: DtType =>
      state.get(dt.name) match {
        case Some(prev) =>
          sender ! Some(prev)
        case _ =>
          state = state + (dt.name -> dt)
          sender ! Some(dt)
          persistAsync(dt) { _ =>
            takeSnapshot()
          }
      }

    case typeName: String =>
      state.get(typeName) match {
        case Some(dt) =>
          sender ! Some(dt)
        case _ =>
          sender ! None
      }

    case m =>
      logger.warn(s"I don't know how to handle $m")
      sender ! None

  }

  override def receiveRecover: Receive = {

    case dt: DtType =>
      Observer("recovered_items_from_jrnl")
      state = state + (dt.name -> dt)

    case SnapshotOffer(_, s: Map[String, DtType] @unchecked) =>
      Observer("recovered_state_from_snapshot")
      state = s

    case _: RecoveryCompleted =>
      logger.debug(s"${self.path}: Recovery completed. State: $state")

    case x =>
      logger.warn(s"I don't know how to handle recover msg: $x")

  }

}
