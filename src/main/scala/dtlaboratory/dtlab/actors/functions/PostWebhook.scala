package dtlaboratory.dtlab.actors.functions

import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model._
import akka.http.scaladsl.{Http, HttpExt}
import dtlaboratory.dtlab.HttpSupport
import dtlaboratory.dtlab.models.JsonSupport
import dtlaboratory.dtlab.Conf._

import scala.concurrent.Future

object PostWebhook extends JsonSupport with HttpSupport {

  val http: HttpExt = Http(system)

  /*
  def applyPost(request: HttpRequest,
                path: String,
                event: EventMsg): Future[HttpResponse] = {
    val newUri =
      request.uri
        .withHost(dtlabHost)
        .withPort(dtlabPort)
        .withPath(Path("/" + urlpath + "/actor" + path))
    logger.debug(s"sending telemetry to: " + newUri)
    val newRequest = request.copy(
      uri = newUri,
      entity =
        HttpEntity(ContentTypes.`application/json`, telem.toJson.compactPrint))
    logger.debug(s"sending request to: " + newRequest)
    http.singleRequest(newRequest)
  }

  // does this make sense?  is this how bad scala really is????
  // ensure that futures are executed 1 at a time
  // we are not so concerned about the latency for a single batch - system scales horizontally when there
  // are lots of writers/posters.
  def seqFutures[T, U](items: TraversableOnce[T])(
      yourfunction: T => Future[U]): Future[List[U]] = {
    items.foldLeft(Future.successful[List[U]](Nil)) { (f, item) =>
      f.flatMap { x =>
        yourfunction(item).map(_ :: x)
      }
    } map (_.reverse)
  }

  def apply(request: HttpRequest,
            telemetry: Seq[(String, Telemetry)]): Future[Seq[HttpResponse]] = {

    seqFutures[(String, Telemetry), HttpResponse](telemetry)(
      (i: (String, Telemetry)) => {
        applyPost(request, i._1, i._2)
      })

  }

   */
}
