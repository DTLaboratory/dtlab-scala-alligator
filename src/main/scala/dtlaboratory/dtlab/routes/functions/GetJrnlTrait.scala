package dtlaboratory.dtlab.routes.functions

import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.pattern._
import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.models._
import dtlaboratory.dtlab.models
import dtlaboratory.dtlab.Conf._
import TelemetryMarshallers._
import dtlaboratory.dtlab.observe.Observer

/**
 * query the event sourced persistence with this API with offset/limit bookmarking
 */
trait GetJrnlTrait extends Directives with JsonSupport with LazyLogging {

  private def jrnl(dtp: DtPath,
                   limit: Int,
                   offset: Int,
                   marshal: Marshaller): Route = get {
    onSuccess(dtDirectory ask GetJrnl(dtp, limit, offset)) {
      case s: Seq[Telemetry] @unchecked =>
        Observer("actor_route_jrnl_success")
        onSuccess(marshal(s, dtp.endTypeName(), dtp)) {
          case Some(r) =>
            complete(HttpEntity(ContentTypes.`application/json`, r))
          case _ =>
            complete(StatusCodes.InternalServerError)
        }
      case DtErr(emsg) =>
        Observer("actor_route_jrnl_failure")
        complete(StatusCodes.NotFound, emsg)
      case e =>
        Observer("actor_route_jrnl_unk_err")
        logger.warn(s"unable to handle: $e")
        complete(StatusCodes.InternalServerError)
    }
  }

  private def applyJrnl(segs: List[String],
                        limit: Int,
                        offset: Int,
                        marshall: Marshaller): Route = {
    models.DtPath(segs) match {
      case p: Some[DtPath] =>
        jrnl(dtlaboratory.dtlab.models
               .DtPath(new DtTypeName("root"), new DtInstanceName("root"), p),
             limit,
             offset,
             marshall)
      case _ =>
        logger.warn(s"can not extract DtPath from $segs")
        Observer("bad_request")
        complete(StatusCodes.BadRequest)
    }
  }

  def handleGetJrnl(segs: List[String], limit: Int, offset: Int): Route =
    parameters('format.?) { format =>
      {
        format match {
          case Some("pathed") =>
            Observer("actor_route_jrnl_pathed")
            applyJrnl(segs, limit, offset, pathedFmt)
          case Some("named") =>
            Observer("actor_route_jrnl_named")
            applyJrnl(segs, limit, offset, namedFmt)
          case _ =>
            Observer("actor_route_jrnl_idx")
            applyJrnl(segs, limit, offset, indexedFmt)
        }
      }
    }

}
