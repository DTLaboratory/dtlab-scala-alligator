package somind.dtlab.routes.functions

import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.pattern.ask
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.Conf._
import somind.dtlab.models._
import somind.dtlab.observe.Observer
import somind.dtlab.routes.functions.TelemetryUnMarshallers._

object TelemetryUnWrappers
    extends JsonSupport
    with Directives
    with LazyLogging {

  type UnWrapper = DtPath => Route
  def applyTelemetryMsg(dtp: DtPath, telemetry: Telemetry): Route = {
    onSuccess(dtDirectory ask TelemetryMsg(dtp, telemetry)) {
      case DtOk() =>
        Observer("actor_route_post_success")
        complete(StatusCodes.Accepted)
      case DtErr(emsg) =>
        Observer("actor_route_post_failure")
        logger.debug(s"unable to post telemetry: $emsg")
        complete(StatusCodes.BadRequest, emsg)
      case e =>
        Observer("actor_route_post_unk_err")
        logger.warn(s"unable to handle: $e")
        complete(StatusCodes.InternalServerError)
    }
  }

  def NamedUnWrapper(dtp: DtPath): Route = {
    entity(as[LazyNamedTelemetry]) { ntelem =>
      {
        onSuccess(NamedUnMarshaller(Some(ntelem.telemetry()), dtp)) {
          case Some(telemetry: Telemetry) =>
            applyTelemetryMsg(dtp, telemetry)
          case _ =>
            logger.warn(s"can not unmarshall telemetry")
            complete(
              StatusCodes.BadRequest,
              s"Can not validate - please check that type definition '${dtp
                .endTypeName()}' exists and supports property '${ntelem.name}'.")
        }
      }
    }
  }

  def IdxUnWrapper(dtp: DtPath): Route = {
    entity(as[LazyTelemetry]) { telem =>
      applyTelemetryMsg(dtp, telem.telemetry())
    }
  }

}
