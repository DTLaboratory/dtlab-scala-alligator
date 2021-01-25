package dtlaboratory.dtlab.actors

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.persistence._
import dtlaboratory.dtlab.actors.functions.PostWebhook
import dtlaboratory.dtlab.models._
import dtlaboratory.dtlab.observe.Observer

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

class DtWebhooks extends DtPersistentActorBase[DtWebhookMap, DtWebHook] {

  override var state: DtWebhookMap = DtWebhookMap(webhooks = Map())

  def handleWebhook(wh: DtWebHook, sndr: ActorRef): Unit = {
    state.webhooks.get(wh.name.getOrElse("unknown")) match {
      case Some(prev) =>
        sndr ! Some(prev)
      case _ =>
        state = DtWebhookMap(
          state.webhooks + (wh.name.getOrElse("unknown") -> wh))
        persistAsync(wh) { _ =>
          sndr ! Some(wh)
          takeSnapshot()
          logger.debug(s"create / update webhook: $wh")
        }
    }
  }

  def handleDtEvent(ev: DtEvent, sndr: ActorRef): Unit = {
    val evt = ev match {
      case _: Creation => CreationEventType()
      case _ =>  StateChangeEventType()
    }
    dtlaboratory.dtlab.models.DtPath.applyActorRef(sndr) match {
      case Some(dtp) =>
        val eventMsg = DtEventMsg(ev, StateChangeEventType(), dtp)
        state.webhooks.values
          .filter(_.eventType == evt)
          // todo: add a filter for dtpath prefix
          .foreach(wh => {
            logger.debug(s"invoking webhook ${wh.name} for $ev event")
            Await.result(PostWebhook(wh, eventMsg), 3.seconds) match {
              case StatusCodes.Accepted =>
                logger.debug(s"webhook ${wh.name} successful")
              case s =>
                logger.warn(
                  s"webhook ${wh.name} unsuccessful. code: $s payload: $eventMsg")
            }
          })
      case _ =>
        logger.warn(s"can not extract DtPath from sender ${sender()}")
    }
  }

  override def receiveCommand: Receive = {

    case wid: String =>
      state.webhooks.get(wid) match {
        case Some(wh) =>
          sender ! Some(wh)
        case _ =>
          sender ! None
      }

    case DeleteWebhook(wid) =>
      state.webhooks.get(wid) match {
        case Some(prev) =>
          state = DtWebhookMap(state.webhooks - wid)
          sender ! Some(prev)
        case _ =>
          sender ! None
      }

    case wh: DtWebHook => handleWebhook(wh, sender())

    case ev: DtEvent => handleDtEvent(ev, sender())

  }

  override def receiveRecover: Receive = {

    case wh: DtWebHook =>
      Try {
        DtWebhookMap(state.webhooks + (wh.name.getOrElse("unknown") -> wh))
      } match {
        case Success(s) =>
          state = s
          Observer("reapplied_webhook_from_jrnl")
        case Failure(e) =>
          logger.error(s"can not recover: $e", e)
          Observer("reapplied_webhook_from_jrnl_failed")
      }

    case SnapshotOffer(_, snapshot: DtWebhookMap @unchecked) =>
      Try {
        state = snapshot
      } match {
        case Success(_) =>
          Observer("recovered_webhook_state_from_snapshot")
        case Failure(e) =>
          logger.error(s"can not recover: $e", e)
          Observer("recovered_webhook_state_from_snapshot_failure")
      }

    case _: RecoveryCompleted =>
      Observer("resurrected_webhook_actor")
      logger.debug(s"${self.path}: Recovery completed. State: $state")

    case x =>
      logger.warn(s"unexpected recover msg: $x")

  }

}
