package somind.dtlab.models

import java.time.{ZoneOffset, ZonedDateTime}
import java.util.{Date, UUID}

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  val dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX",
                                                  java.util.Locale.US)
  dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"))

  def parse8601(dateString: String): java.util.Date =
    dateFormat.parse(dateString)

  def get8601(date: java.util.Date): String =
    dateFormat.format(date)

  def now8601(): String = {
    val now = new java.util.Date()
    get8601(now)
  }

  implicit object Date extends JsonFormat[Date] {
    def write(dt: java.util.Date): JsValue = JsString(get8601(dt))
    def read(value: JsValue): java.util.Date = {
      value match {
        case JsString(dt) => parse8601(dt)
        case _            => throw DeserializationException("Expected 8601")
      }
    }
  }

  implicit object UUIDFormat extends JsonFormat[UUID] {
    def write(uuid: UUID): JsValue = JsString(uuid.toString)
    def read(value: JsValue): UUID = {
      value match {
        case JsString(uuid) => UUID.fromString(uuid)
        case _ =>
          throw DeserializationException("Expected hexadecimal UUID string")
      }
    }
  }

  implicit object ZonedDateTime extends JsonFormat[ZonedDateTime] {
    def write(dt: ZonedDateTime): JsValue =
      JsString(get8601(new Date(dt.toInstant.toEpochMilli))) // ugh.  replace SimpleDateFormat with new java.time.* stuff
    def read(value: JsValue): ZonedDateTime = {
      value match {
        case JsString(dt) =>
          java.time.ZonedDateTime
            .ofInstant(parse8601(dt).toInstant, ZoneOffset.UTC)
        case _ => throw DeserializationException("Expected 8601")
      }
    }
  }

  implicit val dttFormat: RootJsonFormat[DtType] = jsonFormat4(DtType)
  implicit val tel: RootJsonFormat[Telemetry] = jsonFormat3(Telemetry)
  implicit val ltel: RootJsonFormat[LazyTelemetry] = jsonFormat3(LazyTelemetry)
  implicit val ldt: RootJsonFormat[LazyDtType] = jsonFormat3(LazyDtType)
  implicit val dts: RootJsonFormat[DtState] = jsonFormat1(DtState)

}
