package somind.dtlab.routes.functions

import akka.pattern._
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.Conf._
import somind.dtlab.models._
import spray.json._

import scala.concurrent.Future

/**
 * Enable working with telemetry with meaningful names instead of the index values from
 * the type definition.
 *
 * Each marshaller will lookup the type definition and replace the index field with a text string.
 *
 * A "named" text string is the name of the DT prop.
 * A "pathed" text string is a DtPath with dot notation and the name of the DT prop appended.
 */
object Marshallers extends JsonSupport with LazyLogging {

  type Marshaller = (DtState, String, DtPath) => Future[Option[String]]

  def pathedFmt(s: DtState,
                t: String,
                dtp: DtPath): Future[Option[String]] = {
    val f = dtDirectory ask t
    f.map((r: Any) => {
      r match {
        case Some(dt: DtType) =>
          val names: List[String] = dt.props.getOrElse(Set()).toList
          Some(
            s.state
              .map(i => {
                val namePath: String = s"$dtp/${names(i._1)}"
                val nameDots: String = namePath.replace('/', '.')
                val lookedUpName: String = nameDots.substring(11)
                val origValue = i._2.value
                val origDatetime = i._2.datetime
                NamedTelemetry(lookedUpName, origValue, origDatetime)
              })
              .toJson
              .prettyPrint)
        case _ => None
      }
    })
  }

  def namedFmt(s: DtState,
               t: String,
               dtp: DtPath): Future[Option[String]] = {
    val f = dtDirectory ask t
    f.map((r: Any) => {
      r match {
        case Some(dt: DtType) =>
          val names: List[String] = dt.props.getOrElse(Set()).toList
          Some(
            s.state
              .map(i => {
                val lookedUpName = names(i._1)
                val origValue = i._2.value
                val origDatetime = i._2.datetime
                NamedTelemetry(lookedUpName, origValue, origDatetime)
              })
              .toJson
              .prettyPrint)
        case _ => None
      }
    })
  }

  def indexedFmt(s: DtState, t: String, dtp: DtPath): Future[Option[String]] =
    Future {
      Some(s.state.values.toJson.prettyPrint)
    }

}
