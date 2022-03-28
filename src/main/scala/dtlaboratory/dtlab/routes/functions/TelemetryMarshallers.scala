package dtlaboratory.dtlab.routes.functions

import akka.pattern._
import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.Conf._
import dtlaboratory.dtlab.models._
import spray.json._

import scala.concurrent.Future

/** Enable working with telemetry with meaningful names instead of the index values from
  * the type definition.
  *
  * Each marshaller will lookup the type definition and replace the index field with a text string.
  *
  * A "named" text string is the name of the DT prop.
  * A "pathed" text string is a DtPath with dot notation and the name of the DT prop appended.
  */
object TelemetryMarshallers extends JsonSupport with LazyLogging {

  type Marshaller =
    (Seq[Telemetry], DtTypeName, DtPath) => Future[Option[String]]

  private def fmt(
      s: Seq[Telemetry],
      t: DtTypeName,
      dtp: DtPath,
      dottedName: Boolean = false
  ): Future[Option[Seq[NamedTelemetry]]] = {
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
        )
      case _ => None
    }
  }

  private def nfmt(
      s: Seq[Telemetry],
      t: DtTypeName,
      dtp: DtPath,
      dottedName: Boolean = false
  ): Future[Option[String]] = {

    fmt(s, t, dtp, dottedName).map {
      case Some(s: Seq[NamedTelemetry]) => Some(s.toJson.prettyPrint)
      case _                            => None
    }
  }

  private def ofmt(
      s: Seq[Telemetry],
      t: DtTypeName,
      dtp: DtPath,
      dottedName: Boolean = false
  ): Future[Option[String]] = {

    fmt(s, t, dtp, dottedName).map((r: Option[Seq[NamedTelemetry]]) => {

      val data: Option[JsValue] = r.map((q: Seq[NamedTelemetry]) =>
        q.map(i => i.name -> i.value).toMap.toJson
      )

      data match {
        case Some(dval: JsValue) =>
          val nval = dtp.instanceId.toString.toJson
          val tval = dtp.endTypeName().toString.toJson
          Some(
            Seq(
              "instanceId" -> nval,
              "dtType" -> tval,
              "state" -> dval
            ).toMap.toJson.prettyPrint
          )
        case _ =>
          None
      }

    })
  }

  def objFmt(
      s: Seq[Telemetry],
      t: DtTypeName,
      dtp: DtPath
  ): Future[Option[String]] =
    ofmt(s, t, dtp)

  def namedFmt(
      s: Seq[Telemetry],
      t: DtTypeName,
      dtp: DtPath
  ): Future[Option[String]] =
    nfmt(s, t, dtp)

  def pathedFmt(
      s: Seq[Telemetry],
      t: DtTypeName,
      dtp: DtPath
  ): Future[Option[String]] =
    nfmt(s, t, dtp, dottedName = true)

  //noinspection ScalaUnusedSymbol
  def indexedFmt(
      s: Seq[Telemetry],
      t: DtTypeName,
      dtp: DtPath
  ): Future[Option[String]] =
    Future {
      Some(s.toJson.prettyPrint)
    }

}
