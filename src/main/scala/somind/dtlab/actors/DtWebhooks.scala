package somind.dtlab.actors

import akka.persistence._
import somind.dtlab.models._
import somind.dtlab.observe.Observer

import scala.util.{Failure, Success, Try}

class DtWebhooks extends DtPersistentActorBase[DtWebhookMap, DtWebHook] {

  override var state: DtWebhookMap = DtWebhookMap(webhooks = Map())

  override def receiveCommand: Receive = {

    case wh: DtWebHook =>
      logger.debug(s"create / update webhook: $wh")

    case _: SaveSnapshotSuccess =>
    case m =>
      logger.warn(s"unexpected message: $m")

  }

  override def receiveRecover: Receive = {

    case wh: DtWebHook =>
      Try {
        DtWebhookMap(state.webhooks + (wh.name -> wh))
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
