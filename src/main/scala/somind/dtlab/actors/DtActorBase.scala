package somind.dtlab.actors

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models.{DtMsg, TelemetryMsg}

trait DtActorBase extends Actor with LazyLogging {

  def create(m: TelemetryMsg): Unit =
    context.actorOf(Props[DtActor], m.path().typeId) forward m

  def create(m: DtMsg[Any]): Unit =
    context.actorOf(Props[DtActor], m.path().typeId) forward m

  def upsert(m: TelemetryMsg): Unit =
    context.child(m.path().typeId).fold(create(m))(_ forward m)

  def upsert(m: DtMsg[Any]): Unit =
    context.child(m.path().typeId).fold(create(m))(_ forward m)

}
