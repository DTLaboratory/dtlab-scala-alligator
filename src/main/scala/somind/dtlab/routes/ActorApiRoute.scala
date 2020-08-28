package somind.dtlab.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.pattern._
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.Conf._
import somind.dtlab.HttpSupport
import somind.dtlab.models._
import somind.dtlab.observe.Observer
import spray.json._

import scala.concurrent.Future

object ActorApiRoute
    extends JsonSupport
    with LazyLogging
    with Directives
    with HttpSupport {

  def unmarshalPathed(s: DtState): Future[Option[String]] =
    Future {
      // ejs todo: process type
      Some(s.state.values.toJson.prettyPrint)
    }

  def unmarshalNamed(s: DtState): Future[Option[String]] =
    Future {
      // ejs todo: process type
      Some(s.state.values.toJson.prettyPrint)
    }

  def unmarshalIdx(s: DtState): Future[Option[String]] =
    Future {
      Some(s.state.values.toJson.prettyPrint)
    }

  def applyDtPath(dtp: DtPath,
                  unmarshal: DtState => Future[Option[String]]): Route = {
    get {
      onSuccess(dtDirectory ask DtGetState(dtp)) {
        case s: DtState =>
          Observer("actor_route_get_success")
          onSuccess(unmarshal(s)) {
            case Some(r) =>
              complete(HttpEntity(ContentTypes.`application/json`, r))
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        case DtErr(emsg) =>
          Observer("actor_route_get_failure")
          complete(StatusCodes.NotFound, emsg)
        case e =>
          Observer("actor_route_get_unk_err")
          logger.warn(s"unable to handle: $e")
          complete(StatusCodes.InternalServerError)
      }
    } ~
      delete {
        complete(StatusCodes.NotImplemented)
      } ~
      post {
        decodeRequest {
          entity(as[LazyTelemetry]) { telem =>
            onSuccess(dtDirectory ask TelemetryMsg(dtp, telem.telemetry())) {
              case DtOk() =>
                Observer("actor_route_post_success")
                complete(StatusCodes.Accepted)
              case DtErr(emsg) =>
                Observer("actor_route_post_failure")
                logger.debug(s"unable to post telemetry: $emsg")
                complete(StatusCodes.UnprocessableEntity, emsg)
              case e =>
                Observer("actor_route_post_unk_err")
                logger.warn(s"unable to handle: $e")
                complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
  }

  def applySeq(segs: List[String],
               unmarshall: DtState => Future[Option[String]]): Route = {
    DtPath(segs) match {
      case Some(p) =>
        applyDtPath(DtPath("root", "root", Some(p)), unmarshall)
      case _ =>
        Observer("bad_request")
        complete(StatusCodes.BadRequest)
    }
  }

  def applyUnmarshalling(segs: List[String]): Route =
    path("pathed") {
      applySeq(segs, unmarshalPathed)
    } ~
      path("named") {
        applySeq(segs, unmarshalNamed)
      } ~
      applySeq(segs, unmarshalIdx)

  def apply: Route =
    pathPrefix("actor") {
      pathPrefix(Segments(20)) { segs: List[String] =>
        applyUnmarshalling(segs)
      } ~
        pathPrefix(Segments(18)) { segs: List[String] =>
          applyUnmarshalling(segs)
        } ~
        pathPrefix(Segments(16)) { segs: List[String] =>
          applyUnmarshalling(segs)
        } ~
        pathPrefix(Segments(14)) { segs: List[String] =>
          applyUnmarshalling(segs)
        } ~
        pathPrefix(Segments(12)) { segs: List[String] =>
          applyUnmarshalling(segs)
        } ~
        pathPrefix(Segments(10)) { segs: List[String] =>
          applyUnmarshalling(segs)
        } ~
        pathPrefix(Segments(8)) { segs: List[String] =>
          applyUnmarshalling(segs)
        } ~
        pathPrefix(Segments(6)) { segs: List[String] =>
          applyUnmarshalling(segs)
        } ~
        pathPrefix(Segments(4)) { segs: List[String] =>
          applyUnmarshalling(segs)
        } ~
        pathPrefix(Segments(2)) { segs: List[String] =>
          applyUnmarshalling(segs)
        }

    }

}
