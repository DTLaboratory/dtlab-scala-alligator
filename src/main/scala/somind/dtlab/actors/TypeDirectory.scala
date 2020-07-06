package somind.dtlab.actors

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models.DtType

object TypeDirectory extends LazyLogging {

  def name: String = this.getClass.getName

}

class TypeDirectory extends Actor with LazyLogging {

  var state: Map[String, DtType] = Map()

//  override def persistenceId: String =
//    persistIdRoot + "_" + self.path.toString.replace('-', '_')

  override def receive: Receive = {

    case dt: DtType =>
      state.get(dt.name) match {
        case Some(prev) =>
          sender ! Some(prev)
        case _ =>
          state = state + (dt.name -> dt)
          sender ! Some(dt)
      }

    case typeName: String =>
      state.get(typeName) match {
        case Some(dt)  =>
          sender ! Some(dt)
        case _  =>
          sender ! None
      }

    case m =>
      logger.warn(s"I don't know how to handle $m")
      sender ! None

  }

}
