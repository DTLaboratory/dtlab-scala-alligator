package dtlaboratory.dtlab.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.pattern.ask
import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.Conf._
import dtlaboratory.dtlab.HttpSupport
import dtlaboratory.dtlab.models._
import dtlaboratory.dtlab.observe.Observer
import spray.json._

/**
 * manage webhooks here - apply them globally by eventtype  or specify scope by dt path
 */
object WebhookApiRoute
    extends JsonSupport
    with LazyLogging
    with Directives
    with HttpSupport {

  def apply: Route = {
    path("webhook" / Segment) { wid =>
      get {
        onSuccess(webhooks ask wid) {
          case Some(wh: DtWebHook) =>
            Observer("webhook_route_get_success")
            complete(
              HttpEntity(ContentTypes.`application/json`,
                         wh.toJson.prettyPrint))
          case None =>
            Observer("webhook_route_get_notfound")
            complete(StatusCodes.NotFound)
          case e =>
            Observer("type_route_get_unk_err")
            logger.warn(s"unable to handle: $e")
            complete(StatusCodes.InternalServerError)
        }
      } ~
        delete {
          onSuccess(webhooks ask DeleteWebhook(wid)) {
            case Some(_) =>
              Observer("webhook_route_delete_success")
              complete(StatusCodes.Accepted)
            case None =>
              Observer("webhook_route_get_notfound")
              complete(StatusCodes.NotFound)
            case e =>
              Observer("webhook_route_get_unk_err")
              logger.warn(s"unable to handle: $e")
              complete(StatusCodes.InternalServerError)
          }
        } ~ post {
        decodeRequest {
          entity(as[DtWebHook]) { input =>
            val wh = input.copy(name = Some(wid),
                                created = Some(java.time.ZonedDateTime.now()))
            onSuccess(webhooks ask wh) {
              case Some(newWh: DtWebHook) if wh.created == newWh.created =>
                Observer("webhook_route_post_success")
                complete(StatusCodes.Created,
                         HttpEntity(ContentTypes.`application/json`,
                                    newWh.toJson.prettyPrint))
              case Some(newWh: DtWebHook) if wh.created != newWh.created =>
                Observer("webhook_route_post_dupe_err")
                logger.debug(s"duplicate create request: $wh")
                complete(StatusCodes.Conflict)
              case e =>
                Observer("webhook_route_post_unk_err")
                logger.warn(s"unable to handle: $e")
                complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    }
  }

}
