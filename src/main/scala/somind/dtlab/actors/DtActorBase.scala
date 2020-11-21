package somind.dtlab.actors

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models.{DtChildren, DtMsg, TakeSnapshot}

trait DtActorBase extends Actor with LazyLogging {

  var children: DtChildren

  def create(m: DtMsg[Any], name: String): Unit = {
    if (!children.children.contains(name)) {
      children = DtChildren(children = name :: children.children)
      logger.debug(s"updating children with $name")
      self ! TakeSnapshot()
    }
    logger.debug(s"forwarding '$name' from create function: $m")
    context.actorOf(Props[DtActor], name) forward m
  }

  def upsert(m: DtMsg[Any]): Unit = {

    m.path().trail match {
      case Some(p) =>
        val instanceId = p.instanceId
        if (self.path.name == p.typeId)
          context
            .child(instanceId)
            .fold(create(m.trailMsg(), instanceId))(_ forward m.trailMsg())
        else
          context.child(p.typeId).fold(create(m, p.typeId))(_ forward m)
      case e =>
        throw new UnsupportedOperationException(s"no trail: $e")
    }

  }

}
