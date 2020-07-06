package somind.dtlab.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.HttpSupport
import somind.dtlab.models._

object LinkApiRoute
    extends JsonSupport
    with LazyLogging
    with Directives
    with HttpSupport {

  def apply: Route =
    path(urlpath) {
      logRequest(urlpath) {
        handleErrors {
          cors(corsSettings) {
            get {
              complete(StatusCodes.NotImplemented)
            } ~
              post {
                complete(StatusCodes.NotImplemented)
              }

          }
        }
      }
    }
}
