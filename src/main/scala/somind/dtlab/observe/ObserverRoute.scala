package somind.dtlab.observe

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import akka.pattern.ask
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future
import scala.util.Success
import somind.dtlab.Conf._

object ObserverRoute extends LazyLogging with Directives {

  def apply: Route = {
    path("observe") {
      get {
        val m: Future[Any] = observer ask MeasurementsQuery()
        onComplete(m) {
          case Success(observer: Measurements) =>
            complete(
              HttpEntity(ContentTypes.`text/plain(UTF-8)`,
                         observer.metrics.mkString("\n") + '\n'))
          case e =>
            logger.warn(s"can not get measurements: $e")
            complete(StatusCodes.ServiceUnavailable)
        }
      }
    } ~
      path("fitness") {
        get {
          val m: Future[Any] = observer ask FitnessQuery()
          onComplete(m) {
            case Success(_: Feeble) =>
              complete(StatusCodes.InternalServerError)
            case Success(_: Hardy) =>
              complete(StatusCodes.OK)
            case e =>
              logger.warn(s"unexpected fitness state: $e")
              complete(StatusCodes.ServiceUnavailable)
          }
        }
      }
  }

}
