package somind.dtlab.routes.functions

import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.pattern._
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.Conf._
import somind.dtlab.models._
import somind.dtlab.observe.Observer
import spray.json._

trait OperatorApiTrait extends Directives with JsonSupport with LazyLogging {

  private def applyGet(dtp: DtPath): Route = {
    get {
      onSuccess(dtDirectory ask GetOperators(dtp)) {
        case r: OperatorMap =>
          Observer("actor_route_get_operators_success")
          complete(
            HttpEntity(ContentTypes.`application/json`,
                       r.operators.values.toJson.prettyPrint))
        case DtErr(errorMsg) =>
          Observer("actor_route_get_operators_failure")
          complete(StatusCodes.NotFound, errorMsg)
        case e =>
          Observer("actor_route_get_operators_unk_err")
          logger.warn(s"unable to handle: $e")
          complete(StatusCodes.InternalServerError)
      }
    }
  }

  private def applyDelete(dtp: DtPath): Route = {
    delete {
      Observer("actor_route_operator_delete")
      onSuccess(dtDirectory ask DeleteOperators(dtp)) {
        case _: DtOk =>
          Observer("actor_route_delete_operators_success")
          complete(StatusCodes.Accepted)
        case DtErr(emsg) =>
          Observer("actor_route_delete_operators_failure")
          complete(StatusCodes.NotFound, emsg)
        case e =>
          Observer("actor_route_delete_operators_unk_err")
          logger.warn(s"unable to handle: $e")
          complete(StatusCodes.InternalServerError)
      }
    }
  }

  private def applyPost(dtp: DtPath): Route = {
    post {
      Observer("actor_route_operator_create")
      entity(as[Operator]) { lazyOp =>
        val operator =
          lazyOp.copy(created = Some(java.time.ZonedDateTime.now()))
        onSuccess(dtDirectory ask OperatorMsg(dtp, operator)) {
          case r: OperatorMap =>
            Observer("actor_route_create_operator_success")
            complete(
              HttpEntity(ContentTypes.`application/json`,
                         r.operators.values.toJson.prettyPrint))
          case DtErr(emsg) =>
            Observer("actor_route_create_operator_failure")
            complete(StatusCodes.BadRequest, emsg)
          case e =>
            Observer("actor_route_create_operator_unk_err")
            logger.warn(s"unable to handle: $e")
            complete(StatusCodes.InternalServerError)
        }
      }
    }
  }

  private def applyApiHandlers(dtp: DtPath): Route = {
    applyGet(dtp) ~
      applyDelete(dtp) ~
      applyPost(dtp)
  }

  def handleOperatorApi(segs: List[String]): Route = {
    somind.dtlab.models.DtPath(segs) match {
      case p: Some[DtPath] =>
        applyApiHandlers(
          somind.dtlab.models
            .DtPath(new DtTypeName("root"), new DtInstanceName("root"), p))
      case _ =>
        logger.warn(s"can not extract DtPath from $segs")
        Observer("bad_request")
        complete(StatusCodes.BadRequest)
    }
  }

}
