package somind.dtlab.actors

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models.{DtChildren, DtMsg, TakeSnapshot}

trait DtActorBase extends Actor with LazyLogging {

  var children: DtChildren = DtChildren()
  //var children: DtChildren

  // ejs bug!  seems like this children is not visible - is a shadowed
  // ejs bug!  seems like this children is not visible - is a shadowed
  // ejs bug!  seems like this children is not visible - is a shadowed
  // ejs bug!  seems like this children is not visible - is a shadowed
  // ejs bug!  seems like this children is not visible - is a shadowed
  def create(m: DtMsg[Any], name: String): Unit = {
    logger.debug(s"${self.path} create sees $name and sees children: $children")
    if (!children.children.contains(name)) {
      children = DtChildren(children = name :: children.children)
      logger.debug(s"updating children with $name")
      self ! TakeSnapshot()
    }
    logger.debug(s"${self.path} forwarding '$name' from create function: $m")
    context.actorOf(Props[DtActor], name) forward m
  }

  // bug !!! dtdirectory is flattenning the root - object is a child for some reason that is wrong
  // bug !!! dtdirectory is flattenning the root - object is a child for some reason that is wrong
  // bug !!! dtdirectory is flattenning the root - object is a child for some reason that is wrong
  // bug !!! dtdirectory is flattenning the root - object is a child for some reason that is wrong
  // bug !!! dtdirectory is flattenning the root - object is a child for some reason that is wrong
  def upsert(m: DtMsg[Any]): Unit = {
    logger.debug(s"${self.path} upsert: $m")
    m.path().trail match {
      case Some(p) =>
        val instanceId = p.instanceId
        // note: recursive types not supported yet
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
