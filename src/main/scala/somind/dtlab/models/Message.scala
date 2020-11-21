package somind.dtlab.models
import java.time.ZonedDateTime

object DtPath {
  def apply(segs: List[String]): Option[DtPath] = {
    segs match {
      case s if s.length < 2 => None
      case s =>
        Option(DtPath(s.head, s(1), DtPath(s.drop(2))))
    }
  }
}

case class DtPath(typeId: String,
                  instanceId: String,
                  trail: Option[DtPath] = None) {
  // convenience method to get the final typeName for validation
  def endTypeName(): String = {
    trail match {
      case Some(t) =>
        t.endTypeName()
      case _ => typeId
    }
  }
  def relationships(): List[(String, String)] = {
    trail match {
      case None                         => List()
      case Some(dt) if typeId == "root" => dt.relationships()
      case Some(dt)                     => (typeId, dt.typeId) :: dt.relationships()
    }
  }
  private def pathToString(p: DtPath): String = {
    s"/${p.typeId}/${p.instanceId}" + {
      p.trail match {
        case None =>
          ""
        case Some(t) =>
          pathToString(t)
      }
    }
  }
  override def toString: String = {
    pathToString(this)
  }
}

sealed trait DtResult
final case class DtOk() extends DtResult
final case class DtErr(message: String) extends DtResult

final case class DeleteDtType(typeId: String)

// particular type of a kind
final case class DtType(
    // the name of our type
    name: String,
    // the names of the properties (called props instead of attributes because
    // values of props can change - values of attributes cannot change)
    props: Option[Seq[String]],
    // the ids/names of the types of child actors that this actor type can instantiate
    children: Option[Set[String]],
    // datetime of creation - no updates allowed
    created: ZonedDateTime = ZonedDateTime.now()
)

// for API to avoid setting created
final case class LazyDtType(
    props: Option[Seq[String]],
    children: Option[Set[String]],
    created: Option[ZonedDateTime]
) {
  def dtType(name: String): DtType =
    DtType(name, props, children, created.getOrElse(ZonedDateTime.now()))
}

final case class LazyTelemetry(
    idx: Int,
    value: Double,
    datetime: Option[ZonedDateTime]
) {
  def telemetry(): Telemetry =
    Telemetry(idx, value, datetime.getOrElse(ZonedDateTime.now()))
}

final case class Telemetry(
    idx: Int,
    value: Double,
    datetime: ZonedDateTime = ZonedDateTime.now()
)

final case class LazyNamedTelemetry(
    name: String,
    value: Double,
    datetime: Option[ZonedDateTime]
) {
  def telemetry(): NamedTelemetry =
    NamedTelemetry(name, value, datetime.getOrElse(ZonedDateTime.now()))
}

final case class NamedTelemetry(
    name: String,
    value: Double,
    datetime: ZonedDateTime = ZonedDateTime.now()
)

// collection of all props in an actor instance
final case class DtState(
    state: Map[Int, Telemetry] = Map()
)

// collection of all types in domain
final case class DtTypeMap(
    types: Map[String, DtType]
)

final case class DtGetState(p: DtPath) extends DtMsg[Any] {
  override def path(): DtPath = p
  override def content(): Any = None
  def trailMsg(): DtGetState = p.trail match {
    case Some(tp) => DtGetState(tp)
    case _        => this
  }
}

sealed trait DtMsg[+T] {
  def path(): DtPath
  def content(): T
  def trailMsg(): DtMsg[T]
}

final case class TelemetryMsg(p: DtPath, c: Telemetry)
    extends DtMsg[Telemetry] {
  def path(): DtPath = p
  def content(): Telemetry = c
  def trailMsg(): DtMsg[Telemetry] = p.trail match {
    case Some(tp) =>
      TelemetryMsg(tp, c)
    case _ => this
  }
}

final case class TakeSnapshot()
// collection of all props in an actor instance
final case class DtChildren(
    children: List[String] = List()
)
final case class DtStateHolder[T](
    state: T,
    children: DtChildren
)
