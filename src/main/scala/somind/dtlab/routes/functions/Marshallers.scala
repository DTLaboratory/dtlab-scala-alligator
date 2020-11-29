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

  type Marshaller = (Seq[Telemetry], String, DtPath) => Future[Option[String]]

  private def fmt(s: Seq[Telemetry],
                  t: String,
                  dtp: DtPath,
                  dottedName: Boolean = false): Future[Option[String]] = {
    val f = dtDirectory ask t
    f.map {
      case Some(dt: DtType) =>
        val names: List[String] = dt.props.getOrElse(Set()).toList
        Some(
          s
            .map(telem => {
              val nameIdx = telem.idx
              val origTelem = telem
              val lookedUpName: String = {
                nameIdx match {
                  case idx if idx >= names.length =>
                    "unknown"
                  case idx if dottedName =>
                    s"$dtp/${names(idx)}".replace('/', '.').substring(11)
                  case idx =>
                    names(idx)
                }
              }
              NamedTelemetry(lookedUpName, origTelem.value, origTelem.datetime)
            })
            .toJson
            .prettyPrint)
      case _ => None
    }
  }

  def namedFmt(s: Seq[Telemetry], t: String, dtp: DtPath): Future[Option[String]] =
    fmt(s, t, dtp)

  def pathedFmt(s: Seq[Telemetry], t: String, dtp: DtPath): Future[Option[String]] =
    fmt(s, t, dtp, dottedName = true)

  //noinspection ScalaUnusedSymbol
  def indexedFmt(s: Seq[Telemetry], t: String, dtp: DtPath): Future[Option[String]] =
    Future {
      Some(s.toJson.prettyPrint)
    }

}
