package dtlaboratory.dtlab.actors

import akka.actor.SupervisorStrategy._
import akka.actor._
import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.models._

import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * helper functions that know if a child being updated needs to be created
 *
 * this code also maintains the persisted child list
 */
trait DtActorBase extends Actor with LazyLogging with JsonSupport {

  var children: DtChildren = DtChildren()
  var operators: OperatorMap = OperatorMap()

  def create(m: DtMsg[Any], name: String, persist: Boolean): Unit = {
    logger.debug(s"${self.path} create sees $name and sees children: $children")
    if (persist && m.isInstanceOf[TelemetryMsg] && !children.children.contains(
          name)) {
      children = DtChildren(children = name :: children.children)
      logger.debug(s"updating children with $name")
      self ! TakeSnapshot()
    }
    logger.debug(s"${self.path} forwarding '$name' from create function: $m")
    context.actorOf(Props[DtActor], name) forward m
  }

  def upsert(m: DtMsg[Any]): Unit = {
    m.path().trail match {
      case Some(p) =>
        val instanceId = p.instanceId
        // note: recursive types not supported yet
        if (self.path.name == p.typeId.name)
          context
            .child(instanceId.name)
            .fold(create(m.trailMsg(), instanceId.name, persist = true))(
              _ forward m.trailMsg())
        else
          context.child(p.typeId.name).fold(create(m, p.typeId.name, persist = false))(_ forward m)
      case e =>
        throw new UnsupportedOperationException(s"no trail: $e")
    }
  }

  override def supervisorStrategy: SupervisorStrategy = {
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case e: NullPointerException =>
        logger.error(s"handling npe $e", e)
        Restart
      case t =>
        super.supervisorStrategy.decider.applyOrElse(t, (_: Any) => Escalate)
    }
  }

}
