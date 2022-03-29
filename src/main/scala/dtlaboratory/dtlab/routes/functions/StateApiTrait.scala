package dtlaboratory.dtlab.routes.functions

import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.pattern._
import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.Conf._
import dtlaboratory.dtlab.models
import dtlaboratory.dtlab.models._
import dtlaboratory.dtlab.observe.Observer
import dtlaboratory.dtlab.routes.functions.TelemetryMarshallers._
import dtlaboratory.dtlab.routes.functions.TelemetryUnWrappers._

/**
 * some ugliness to allow the APIs to use verbose type field names instead of indexes of fields
 */
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
    models.DtPath(segs) match {
      case p: Some[DtPath] =>
        applyStateApiHandlers(
          dtlaboratory.dtlab.models
            .DtPath(new DtTypeName("root"), new DtInstanceName("root"), p),
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
          case Some("object") =>
            Observer("actor_route_telemetry_object")
            applyStateApiFmt(segs, objFmt, NamedUnWrapper) // TODO: make for object
          case _ =>
            Observer("actor_route_telemetry_idx")
            applyStateApiFmt(segs, indexedFmt, IdxUnWrapper)
        }
      }
    }

}
