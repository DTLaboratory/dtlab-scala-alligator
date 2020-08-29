package somind.dtlab.routes.functions

import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models._
import somind.dtlab.routes.ActorApiRoute.applyTelemetryMsg
import somind.dtlab.routes.functions.UnMarshallers._

object UnWrappers extends JsonSupport with Directives with LazyLogging {

  type UnWrapper = DtPath => Route

  def PathedUnWrapper(dtp: DtPath): Route = {
    entity(as[LazyNamedTelemetry]) { ntelem =>
      {
        onSuccess(PathedUnMarshaller(Some(ntelem.telemetry()), dtp)) {
          case Some(telemetry: Telemetry) =>
            applyTelemetryMsg(dtp, telemetry)
          case _ =>
            logger.warn(s"PathedUnMarshaller did not return telemetry for path: $dtp")
            complete(StatusCodes.InternalServerError)
        }
      }
    }
  }

  def NamedUnWrapper(dtp: DtPath): Route = {
    entity(as[LazyNamedTelemetry]) { ntelem =>
      {
        onSuccess(NamedUnMarshaller(Some(ntelem.telemetry()), dtp)) {
          case Some(telemetry: Telemetry) =>
            applyTelemetryMsg(dtp, telemetry)
          case _ =>
            complete(StatusCodes.InternalServerError)
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
