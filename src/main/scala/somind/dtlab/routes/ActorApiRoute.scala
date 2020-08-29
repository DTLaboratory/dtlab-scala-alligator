package somind.dtlab.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.pattern._
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.Conf._
import somind.dtlab.HttpSupport
import somind.dtlab.models._
import somind.dtlab.observe.Observer
import somind.dtlab.routes.functions.Marshallers._
import somind.dtlab.routes.functions.UnWrappers._

object ActorApiRoute
    extends JsonSupport
    with LazyLogging
    with Directives
    with HttpSupport {

  type UnWrapper = DtPath => Route

  def applyTelemetryMsg(dtp: DtPath, telemetry: Telemetry): Route = {
    onSuccess(dtDirectory ask TelemetryMsg(dtp, telemetry)) {
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

  def applyDtPath(dtp: DtPath,
                  marshal: Marshaller,
                  unWrapper: UnWrapper): Route = {
    get {
      onSuccess(dtDirectory ask DtGetState(dtp)) {
        case s: DtState =>
          Observer("actor_route_get_success")
          onSuccess(marshal(s, dtp.endTypeName(), dtp)) {
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
          unWrapper(dtp)
        }
      }
  }

  def applySeq(segs: List[String],
               marshall: Marshaller,
               unWrapper: UnWrapper): Route = {
    somind.dtlab.models.DtPath(segs) match {
      case Some(p) =>
        applyDtPath(somind.dtlab.models.DtPath("root", "root", Some(p)),
                    marshall,
                    unWrapper)
      case _ =>
        Observer("bad_request")
        complete(StatusCodes.BadRequest)
    }
  }

  def applySegs(segs: List[String]): Route =
    path("pathed") {
      applySeq(segs, pathedFmt, PathedUnWrapper)
    } ~
      path("named") {
        applySeq(segs, namedFmt, NamedUnWrapper)
      } ~
      applySeq(segs, indexedFmt, IdxUnWrapper)

  def apply: Route =
    pathPrefix("actor") {
      pathPrefix(Segments(20)) { segs: List[String] =>
        applySegs(segs)
      } ~
        pathPrefix(Segments(18)) { segs: List[String] =>
          applySegs(segs)
        } ~
        pathPrefix(Segments(16)) { segs: List[String] =>
          applySegs(segs)
        } ~
        pathPrefix(Segments(14)) { segs: List[String] =>
          applySegs(segs)
        } ~
        pathPrefix(Segments(12)) { segs: List[String] =>
          applySegs(segs)
        } ~
        pathPrefix(Segments(10)) { segs: List[String] =>
          applySegs(segs)
        } ~
        pathPrefix(Segments(8)) { segs: List[String] =>
          applySegs(segs)
        } ~
        pathPrefix(Segments(6)) { segs: List[String] =>
          applySegs(segs)
        } ~
        pathPrefix(Segments(4)) { segs: List[String] =>
          applySegs(segs)
        } ~
        pathPrefix(Segments(2)) { segs: List[String] =>
          applySegs(segs)
        }

    }

}
