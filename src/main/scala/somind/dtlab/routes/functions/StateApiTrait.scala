package somind.dtlab.routes.functions

import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.pattern._
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.Conf._
import somind.dtlab.models._
import somind.dtlab.observe.Observer
import somind.dtlab.routes.functions.TelemetryMarshallers._
import somind.dtlab.routes.functions.TelemetryUnWrappers._

trait StateApiTrait extends Directives with JsonSupport with LazyLogging {

  private type UnWrapper = DtPath => Route

  private def applyStateApiHandlers(dtp: DtPath,
                              marshal: Marshaller,
                              unWrapper: UnWrapper): Route = {
    get {
      onSuccess(dtDirectory ask GetState(dtp)) {
        case s: DtState =>
          Observer("actor_route_get_success")
          onSuccess(marshal(s.state.values.toSeq, dtp.endTypeName(), dtp)) {
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
        Observer("actor_route_delete")
        complete(StatusCodes.NotImplemented)
      } ~
      post {
        decodeRequest {
          unWrapper(dtp)
        }
      }
  }

  private def applyStateApiFmt(segs: List[String],
                                 marshall: Marshaller,
                                 unWrapper: UnWrapper): Route = {
    somind.dtlab.models.DtPath(segs) match {
      case p: Some[DtPath] =>
        applyStateApiHandlers(somind.dtlab.models.DtPath("root", "root", p),
                        marshall,
                        unWrapper)
      case _ =>
        logger.warn(s"can not extract DtPath from $segs")
        Observer("bad_request")
        complete(StatusCodes.BadRequest)
    }
  }

  def handleStateApi(segs: List[String]): Route =
    parameters('format.?) { format =>
      {
        format match {
          case Some("pathed") =>
            Observer("actor_route_telemetry_pathed")
            applyStateApiFmt(segs, pathedFmt, NamedUnWrapper)
          case Some("named") =>
            Observer("actor_route_telemetry_named")
            applyStateApiFmt(segs, namedFmt, NamedUnWrapper)
          case _ =>
            Observer("actor_route_telemetry_idx")
            applyStateApiFmt(segs, indexedFmt, IdxUnWrapper)
        }
      }
    }

}