package somind.dtlab.routes.functions

import akka.pattern._
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.Conf._
import somind.dtlab.models._
import spray.json._

import scala.concurrent.Future

object Unmarshallers extends JsonSupport with LazyLogging {

  def unmarshalPathed(s: DtState,
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
                val name: String =
                  s"$dtp/${names(i._1)}".replace('/', '.').substring(11)
                NamedTelemetry(name, i._2.value, i._2.datetime)
              })
              .toJson
              .prettyPrint)
        case _ => None
      }
    })
  }

  def unmarshalNamed(s: DtState,
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
                NamedTelemetry(names(i._1), i._2.value, i._2.datetime)
              })
              .toJson
              .prettyPrint)
        case _ => None
      }
    })
  }

  def unmarshalIdx(s: DtState, t: String, dtp: DtPath): Future[Option[String]] =
    Future {
      Some(s.state.values.toJson.prettyPrint)
    }

}
