package dtlaboratory.dtlab.actors

import akka.persistence._
import dtlaboratory.dtlab.models._
import dtlaboratory.dtlab.observe.Observer

import scala.util.{Failure, Success, Try}

class DtWebhooks extends DtPersistentActorBase[DtWebhookMap, DtWebHook] {

  override var state: DtWebhookMap = DtWebhookMap(webhooks = Map())

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

    case wh: DtWebHook =>
      state.webhooks.get(wh.name.getOrElse("unknown")) match {
        case Some(prev) =>
          sender ! Some(prev)
        case _ =>
          state = DtWebhookMap(state.webhooks + (wh.name.getOrElse("unknown") -> wh))
          persistAsync(wh) { _ =>
            sender ! Some(wh)
            takeSnapshot()
            logger.debug(s"create / update webhook: $wh")
          }
      }

    case _: StateChange =>
      state.webhooks.values
        .filter(_.eventType == StateChange())
        .foreach(wh => {
          logger.debug(
            s"testing state-change webhook ${wh.name} against event from ${sender()}")
          // ejs todo invoke webhook
        })

    case _: Creation =>
      state.webhooks.values
        .filter(_.eventType == Creation())
        .foreach(wh => {
          logger.debug(
            s"testing creation webhook ${wh.name} against event from ${sender()}")
          // ejs todo invoke webhook
        })

    case _: SaveSnapshotSuccess =>
    case m =>
      logger.warn(s"unexpected message: $m from ${sender()}")

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
