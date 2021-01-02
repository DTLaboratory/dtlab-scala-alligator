package somind.dtlab.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.pattern.ask
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.Conf._
import somind.dtlab.HttpSupport
import somind.dtlab.models._
import somind.dtlab.observe.Observer
import spray.json._

object TypeApiRoute
    extends JsonSupport
    with LazyLogging
    with Directives
    with HttpSupport {

  def apply: Route = {
    path("type" / Segment) { tid =>
      get {
        val typeId = new DtTypeName(tid)
        onSuccess(dtDirectory ask typeId) {
          case Some(currentType: DtType) =>
            Observer("type_route_get_success")
            complete(
              HttpEntity(ContentTypes.`application/json`,
                         currentType.toJson.prettyPrint))
          case None =>
            Observer("type_route_get_notfound")
            complete(StatusCodes.NotFound)
          case e =>
            Observer("type_route_get_unk_err")
            logger.warn(s"unable to handle: $e")
            complete(StatusCodes.InternalServerError)
        }
      } ~
        delete {
          val typeId = new DtTypeName(tid)
          onSuccess(dtDirectory ask DeleteDtType(typeId)) {
            case DtOk() =>
              Observer("type_route_delete_success")
              complete(StatusCodes.Accepted)
            case None =>
              Observer("type_route_get_notfound")
              complete(StatusCodes.NotFound)
            case e =>
              Observer("type_route_get_unk_err")
              logger.warn(s"unable to handle: $e")
              complete(StatusCodes.InternalServerError)
          }
        } ~ post {
        decodeRequest {
          entity(as[LazyDtType]) { ldt =>
            val typeId = new DtTypeName(tid)
            val newType = ldt.dtType(typeId)
            onSuccess(dtDirectory ask newType) {
              case Some(currentType: DtType)
                  if currentType.created == newType.created =>
                Observer("type_route_post_success")
                complete(StatusCodes.Created,
                         HttpEntity(ContentTypes.`application/json`,
                                    currentType.toJson.prettyPrint))
              case Some(currentType: DtType)
                  if currentType.created != newType.created =>
                Observer("type_route_post_dupe_err")
                logger.debug(s"duplicate create request: $currentType")
                complete(StatusCodes.Conflict)
              case e =>
                Observer("type_route_post_unk_err")
                logger.warn(s"unable to handle: $e")
                complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    }
  }

}
