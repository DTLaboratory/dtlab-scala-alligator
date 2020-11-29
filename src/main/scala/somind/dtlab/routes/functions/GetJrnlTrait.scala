package somind.dtlab.routes.functions

import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.pattern._
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.Conf._
import somind.dtlab.models._
import somind.dtlab.observe.Observer
import spray.json._

trait GetJrnlTrait extends Directives with JsonSupport with LazyLogging {

  private def applyGetDtJrnl(dtp: DtPath, limit: Int): Route = {
    get {
      onSuccess(dtDirectory ask DtGetJrnl(dtp, limit)) {
        case j: Seq[Telemetry] @unchecked =>
          Observer("actor_route_get_jrnl_success")
          complete(
            HttpEntity(ContentTypes.`application/json`, j.toJson.prettyPrint))
        case DtErr(emsg) =>
          Observer("actor_route_get_jrnl_failure")
          complete(StatusCodes.NotFound, emsg)
        case e =>
          Observer("actor_route_get_jrnl_unk_err")
          logger.warn(s"unable to handle: $e")
          complete(StatusCodes.InternalServerError)
      }
    }
  }

  def handleGetJrnl(segs: List[String], limit: Int): Route = {
    somind.dtlab.models.DtPath(segs) match {
      case p: Some[DtPath] =>
        applyGetDtJrnl(somind.dtlab.models.DtPath("root", "root", p), limit)
      case _ =>
        logger.warn(s"can not extract DtPath from $segs")
        Observer("bad_request")
        complete(StatusCodes.BadRequest)
    }
  }

}
