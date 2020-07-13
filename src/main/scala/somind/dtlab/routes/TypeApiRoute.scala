package somind.dtlab.routes

import spray.json._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import akka.pattern.ask
import somind.dtlab.Conf._
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.HttpSupport
import somind.dtlab.models._
import somind.dtlab.observe.Observer

object TypeApiRoute
    extends JsonSupport
    with LazyLogging
    with Directives
    with HttpSupport {

  def apply: Route = {
    path("type" / Segment) { typeName =>
      get {
        onSuccess(dtDirectory ask typeName) {
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
      } ~ post {
        decodeRequest {
          entity(as[LazyDtType]) { ldt =>
            val newType = ldt.dtType(typeName)
            onSuccess(dtDirectory ask newType) {
              case Some(currentType: DtType)
                  if currentType.created == newType.created =>
                Observer("type_route_post_success")
                complete(HttpEntity(ContentTypes.`application/json`,
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
