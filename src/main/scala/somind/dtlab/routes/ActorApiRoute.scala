package somind.dtlab.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.pattern._
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.Conf._
import somind.dtlab.HttpSupport
import somind.dtlab.models._
import somind.dtlab.observe.Observer
import somind.dtlab.routes.functions.Marshallers._
import somind.dtlab.routes.functions.UnWrappers._
import spray.json._

/**
  * Enables CRUD for actors and their states.
  *
  * Actors are automatically created if you post telemetry to
  * them.  If they already exist, the new telemetry gets added
  * to their journal and is reflected in the current state view.
  *
  * (When implemented), DELETE will remove the journals of the actor.
  *
  * Telemetry may be expressed in 3 ways:
  *   1. indexed (the native internal format)
  *   2. named
  *   3. pathed
  *
  * When addressing an actor, suffix the path with named or pathed to
  * use the named or pathed telemetry model.
  *
  */
object ActorApiRoute
    extends JsonSupport
    with LazyLogging
    with Directives
    with HttpSupport {

  type UnWrapper = DtPath => Route

  def applyTelemetryMsg(dtp: DtPath, telemetry: Telemetry): Route = {
    onSuccess(dtDirectory ask TelemetryMsg(dtp, telemetry)) {
      case DtOk() =>
        Observer("actor_route_post_success")
        complete(StatusCodes.Accepted)
      case DtErr(emsg) =>
        Observer("actor_route_post_failure")
        logger.debug(s"unable to post telemetry: $emsg")
        complete(StatusCodes.UnprocessableEntity, emsg)
      case e =>
        Observer("actor_route_post_unk_err")
        logger.warn(s"unable to handle: $e")
        complete(StatusCodes.InternalServerError)
    }
  }

  def applyDtPath(dtp: DtPath,
                  marshal: Marshaller,
                  unWrapper: UnWrapper): Route = {
    get {
      onSuccess(dtDirectory ask DtGetState(dtp)) {
        case s: DtState =>
          Observer("actor_route_get_success")
          onSuccess(marshal(s, dtp.endTypeName(), dtp)) {
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

  def applyFmt(segs: List[String],
               marshall: Marshaller,
               unWrapper: UnWrapper): Route = {
    somind.dtlab.models.DtPath(segs) match {
      case p: Some[DtPath] =>
        applyDtPath(somind.dtlab.models.DtPath("root", "root", p),
                    marshall,
                    unWrapper)
      case _ =>
        logger.warn(s"can not extract DtPath from $segs")
        Observer("bad_request")
        complete(StatusCodes.BadRequest)
    }
  }

  def applySegs(segs: List[String]): Route =
    parameters('format.?) { format =>
      {
        format match {
          case Some("pathed") =>
            Observer("actor_route_telemetry_pathed")
            applyFmt(segs, pathedFmt, NamedUnWrapper)
          case Some("named") =>
            Observer("actor_route_telemetry_named")
            applyFmt(segs, namedFmt, NamedUnWrapper)
          case _ =>
            Observer("actor_route_telemetry_idx")
            applyFmt(segs, indexedFmt, IdxUnWrapper)
        }
      }
    }

  def applyChildrenFmt(dtp: DtPath): Route = {
    get {
      onSuccess(dtDirectory ask DtGetChildrenNames(dtp)) {
        case c: DtChildren =>
          Observer("actor_route_get_children_names_success")
          complete(HttpEntity(ContentTypes.`application/json`, c.toJson.prettyPrint))
        case DtErr(emsg) =>
          Observer("actor_route_get_failure")
          complete(StatusCodes.NotFound, emsg)
        case e =>
          Observer("actor_route_get_unk_err")
          logger.warn(s"unable to handle: $e")
          complete(StatusCodes.InternalServerError)
      }
    }
  }

  def applyChildSegs(segs: List[String]): Route = {
    logger.debug(s"ejs get children **********************************")
    Observer("actor_route_children_query")
    somind.dtlab.models.DtPath(segs :+ "children") match {
      case Some(p: DtPath) => applyChildrenFmt(p)
      case _ =>
        logger.warn(s"can not extract DtPath from $segs")
        Observer("bad_request")
        complete(StatusCodes.BadRequest)
    }
  }

  def apply: Route =
    pathPrefix("actor") {
      pathPrefix(Segments(20)) { segs: List[String] =>
        applySegs(segs)
      } ~
        pathPrefix(Segments(19)) { segs: List[String] =>
          applyChildSegs(segs)
        } ~
        pathPrefix(Segments(18)) { segs: List[String] =>
          applySegs(segs)
        } ~
        pathPrefix(Segments(17)) { segs: List[String] =>
          applyChildSegs(segs)
        } ~
        pathPrefix(Segments(16)) { segs: List[String] =>
          applySegs(segs)
        } ~
        pathPrefix(Segments(15)) { segs: List[String] =>
          applyChildSegs(segs)
        } ~
        pathPrefix(Segments(14)) { segs: List[String] =>
          applySegs(segs)
        } ~
        pathPrefix(Segments(13)) { segs: List[String] =>
          applyChildSegs(segs)
        } ~
        pathPrefix(Segments(12)) { segs: List[String] =>
          applySegs(segs)
        } ~
        pathPrefix(Segments(11)) { segs: List[String] =>
          applyChildSegs(segs)
        } ~
        pathPrefix(Segments(10)) { segs: List[String] =>
          applySegs(segs)
        } ~
        pathPrefix(Segments(9)) { segs: List[String] =>
          applyChildSegs(segs)
        } ~
        pathPrefix(Segments(8)) { segs: List[String] =>
          applySegs(segs)
        } ~
        pathPrefix(Segments(7)) { segs: List[String] =>
          applyChildSegs(segs)
        } ~
        pathPrefix(Segments(6)) { segs: List[String] =>
          applySegs(segs)
        } ~
        pathPrefix(Segments(5)) { segs: List[String] =>
          applyChildSegs(segs)
        } ~
        pathPrefix(Segments(4)) { segs: List[String] =>
          applySegs(segs)
        } ~
        pathPrefix(Segments(3)) { segs: List[String] =>
          applyChildSegs(segs)
        } ~
        pathPrefix(Segments(2)) { segs: List[String] =>
          applySegs(segs)
        } ~
        pathPrefix(Segments(1)) { segs: List[String] =>
          applyChildSegs(segs)
        }

    }

}
