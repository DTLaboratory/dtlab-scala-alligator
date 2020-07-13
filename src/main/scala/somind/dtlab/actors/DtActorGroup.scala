package somind.dtlab.actors

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models.DtMsg

class DtActorGroup extends Actor with LazyLogging {

  def create(m: DtMsg[Any]): Unit =
    context.actorOf(Props[DtActor], m.path().instanceId) forward m

  def upsert(m: DtMsg[Any]): Unit =
    context.child(m.path().instanceId).fold(create(m))(_ forward m)

  override def receive: Receive = {

    case m: DtMsg[Any @unchecked] => upsert(m)

    case m =>
      logger.warn(s"unexpected message: $m")

  }

}
