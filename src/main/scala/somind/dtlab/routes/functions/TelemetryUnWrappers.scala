package somind.dtlab.routes.functions

import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models._
import somind.dtlab.routes.ActorApiRoute.applyTelemetryMsg
import somind.dtlab.routes.functions.TelemetryUnMarshallers._

object TelemetryUnWrappers extends JsonSupport with Directives with LazyLogging {

  type UnWrapper = DtPath => Route

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
