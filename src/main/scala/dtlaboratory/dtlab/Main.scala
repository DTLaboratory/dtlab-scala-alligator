package dtlaboratory.dtlab

import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.routes._
import Conf._
import dtlaboratory.dtlab.models.JsonSupport
import dtlaboratory.dtlab.observe.ObserverRoute

/** enter here
  */
object Main extends LazyLogging with JsonSupport with HttpSupport {

  def main(args: Array[String]): Unit = {

    val route =
      ObserverRoute.apply ~
        handleErrors {
          cors(corsSettings) {
            handleErrors {
              logRequest(urlpath) {
                pathPrefix(urlpath) {
                  ignoreTrailingSlash {
                    TypeApiRoute.apply ~
                      WebhookApiRoute.apply ~
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
