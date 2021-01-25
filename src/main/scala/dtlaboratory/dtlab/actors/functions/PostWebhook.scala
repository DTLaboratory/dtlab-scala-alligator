package dtlaboratory.dtlab.actors.functions

import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model._
import akka.http.scaladsl.{Http, HttpExt}
import dtlaboratory.dtlab.Conf._
import dtlaboratory.dtlab.HttpSupport
import dtlaboratory.dtlab.models._
import spray.json._

import scala.concurrent.Future

object PostWebhook extends JsonSupport with HttpSupport {

  val http: HttpExt = Http(system)

  def apply(webhook: DtWebHook, event: DtEventMsg): Future[StatusCode] = {
    val newUri =
      HttpRequest(
        method = HttpMethods.POST,
        entity = HttpEntity(ContentTypes.`application/json`,
                            event.toJson.compactPrint),
        uri = Uri()
          .withHost(webhook.target.host)
          .withPort(webhook.target.port)
          .withScheme(if (webhook.target.tls) "https" else "http")
          .withPath(Path(webhook.target.path))
      )
    logger.debug(s"posting webhook ${webhook.name} to " + newUri)

    http
      .singleRequest(newUri)
      .map(r => {
        r.status match {
          case StatusCodes.NotFound =>
            logger.warn(
              s"could not post webhook. remote service ${newUri.uri} reports path not found")
          case s if s.isFailure() =>
            logger.warn(s"could not post webhook. $r")
          case _ =>
            //noop
        }
        r.status
      })
  }

}
