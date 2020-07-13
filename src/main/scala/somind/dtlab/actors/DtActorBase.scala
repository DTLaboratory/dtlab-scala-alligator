package somind.dtlab.actors

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models.DtMsg

trait DtActorBase extends Actor with LazyLogging {

  def create(m: DtMsg[Any]): Unit =
    context.actorOf(Props[DtActor], m.path().typeId) forward m

  def upsert(m: DtMsg[Any]): Unit =
    context.child(m.path().typeId).fold(create(m))(_ forward m)

}
