package somind.dtlab.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.pattern._
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.Conf._
import somind.dtlab.HttpSupport
import somind.dtlab.models._
import somind.dtlab.observe.Observer
import spray.json._

object ActorApiRoute
    extends JsonSupport
    with LazyLogging
    with Directives
    with HttpSupport {

  def applyDtPath(dtp: DtPath): Route = {
    get {
      onSuccess(dtDirectory ask DtGetState(dtp)) {
        case s: DtState =>
          Observer("type_route_get_success")
          complete(
            HttpEntity(ContentTypes.`application/json`, s.state.toJson.prettyPrint))
        case e =>
          Observer("actor_route_get_unk_err")
          logger.warn(s"unable to handle: $e")
          complete(StatusCodes.InternalServerError)
      }
    } ~
      post {
        complete(StatusCodes.NotImplemented)
      }
  }

  def applySeq(segs: List[String]): Route = {
    DtPath(segs) match {
      case Some(p) => // the root path is: /root/typeId/ + p so that the root has children for each root type
        applyDtPath(DtPath("root", "root", Some(p)))
      case _ =>
        Observer("bad_request")
        complete(StatusCodes.BadRequest)
    }
  }

  def apply: Route =
    path("actor" / Segments(2)) { segs =>
      applySeq(segs)
    } ~
      path("actor" / Segments(4)) { segs =>
        applySeq(segs)
      } ~
      path("actor" / Segments(6)) { segs =>
        applySeq(segs)
      } ~
      path("actor" / Segments(8)) { segs =>
        applySeq(segs)
      } ~
      path("actor" / Segments(10)) { segs =>
        applySeq(segs)
      } ~
      path("actor" / Segments(12)) { segs =>
        applySeq(segs)
      } ~
      path("actor" / Segments(14)) { segs =>
        applySeq(segs)
      } ~
      path("actor" / Segments(16)) { segs =>
        applySeq(segs)
      } ~
      path("actor" / Segments(18)) { segs =>
        applySeq(segs)
      } ~
      path("actor" / Segments(20)) { segs =>
        applySeq(segs)
      }

}
