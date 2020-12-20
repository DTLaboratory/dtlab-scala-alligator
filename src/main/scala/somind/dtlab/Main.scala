package somind.dtlab

import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models.JsonSupport
import somind.dtlab.routes._
import Conf._
import somind.dtlab.observe.ObserverRoute

object Main extends LazyLogging with JsonSupport with HttpSupport {

  def main(args: Array[String]) {

    val route =
      ObserverRoute.apply ~
        handleErrors {
          cors(corsSettings) {
            handleErrors {
              logRequest(urlpath) {
                pathPrefix(urlpath) {
                  ignoreTrailingSlash {
                    TypeApiRoute.apply ~
                      ActorApiRoute.apply
                  }
                }
              }
            }
          }
        }

    Http().newServerAt("0.0.0.0", port).bindFlow(route)
  }
}
