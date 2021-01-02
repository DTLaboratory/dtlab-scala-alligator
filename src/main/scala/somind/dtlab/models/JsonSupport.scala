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

  implicit object DtEventType extends JsonFormat[DtEventType] {
    def write(tp: DtEventType): JsValue = tp match {
      case _: Creation    => JsString("Creation")
      case _: StateChange => JsString("StateChange")
      case _: NewChild    => JsString("NewChild")
      case _: Destruction => JsString("Destruction")
      case _ =>
        throw DeserializationException("Expected DtEventType")
    }
    def read(value: JsValue): DtEventType = {
      value match {
        case JsString("Creation")    => Creation()
        case JsString("StateChange") => StateChange()
        case JsString("NewChild")    => NewChild()
        case JsString("Destruction") => Destruction()
        case _ =>
          throw DeserializationException("Expected DtEventType string")
      }
    }
  }

  implicit object DtTypeName extends JsonFormat[DtTypeName] {
    def write(name: DtTypeName): JsValue = JsString(name.name)
    def read(value: JsValue): DtTypeName = {
      value match {
        case JsString(s) => new DtTypeName(s)
        case _ =>
          throw DeserializationException("Expected DtTypeName string")
      }
    }
  }

  implicit object DtPath extends JsonFormat[DtPath] {
    def write(dtp: DtPath): JsValue = JsString(dtp.toString)
    def read(value: JsValue): DtPath = {
      value match {
        case JsString(s) =>
          somind.dtlab.models.DtPath(s.split("/").toList) match {
            case Some(dtp) => dtp
            case _ =>
              throw DeserializationException("Expected DtPath string")
          }
        case _ =>
          throw DeserializationException("Expected DtPath string")
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

  implicit val _i01: RootJsonFormat[DtType] = jsonFormat4(DtType)
  implicit val _i02: RootJsonFormat[Telemetry] = jsonFormat3(Telemetry)
  implicit val _i03: RootJsonFormat[TelemetryMsg] = jsonFormat2(TelemetryMsg)
  implicit val _i04: RootJsonFormat[NamedTelemetry] = jsonFormat3(
    NamedTelemetry)
  implicit val _i05: RootJsonFormat[LazyDtType] = jsonFormat2(LazyDtType)
  implicit val _i06: RootJsonFormat[DtState] = jsonFormat1(DtState)
  implicit val _i07: RootJsonFormat[DtChildren] = jsonFormat1(DtChildren)
  implicit val _i08: RootJsonFormat[GetChildrenNames] = jsonFormat1(
    GetChildrenNames)
  implicit val _i09: RootJsonFormat[GetState] = jsonFormat1(GetState)
  implicit val _i10: RootJsonFormat[GetJrnl] = jsonFormat3(GetJrnl)
  implicit val _i11: RootJsonFormat[DtTypeMap] = jsonFormat1(DtTypeMap)
  implicit val _i12: RootJsonFormat[DtStateHolder[DtState]] = jsonFormat3(
    DtStateHolder[DtState])
  implicit val _i13: RootJsonFormat[DtStateHolder[DtTypeMap]] = jsonFormat3(
    DtStateHolder[DtTypeMap])
  implicit val _i14: RootJsonFormat[TakeSnapshot] = jsonFormat0(TakeSnapshot)
  implicit val _i15: RootJsonFormat[Operator] = jsonFormat6(Operator)
  implicit val _i16: RootJsonFormat[OperatorMap] = jsonFormat1(OperatorMap)
  implicit val _i17: RootJsonFormat[GetOperators] = jsonFormat1(GetOperators)
  implicit val _i18: RootJsonFormat[DeleteOperators] = jsonFormat1(
    DeleteOperators)
  implicit val _i19: RootJsonFormat[Creation] = jsonFormat0(Creation)
  implicit val _i20: RootJsonFormat[NewChild] = jsonFormat0(NewChild)
  implicit val _i21: RootJsonFormat[Destruction] = jsonFormat0(Destruction)
  implicit val _i22: RootJsonFormat[StateChange] = jsonFormat0(StateChange)
  implicit val _i23: RootJsonFormat[DtWebHookTarget] = jsonFormat4(
    DtWebHookTarget)
  implicit val _i24: RootJsonFormat[DtWebHook] = jsonFormat6(DtWebHook)
  implicit val _i25: RootJsonFormat[DtWebhookMap] = jsonFormat1(DtWebhookMap)

}
