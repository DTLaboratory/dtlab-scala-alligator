package somind.dtlab.models
import java.time.ZonedDateTime

object DtPath {
   def apply(segs: List[String]): Option[DtPath] = {
     segs match {
       case s if s.length < 2 =>  None
       case s =>
         Option(DtPath(s.head, s(1), DtPath(s.drop(2))))
     }
   }
}

case class DtPath(typeId: String, instanceId: String, trail: Option[DtPath] = None) {
  // convenience method to get the final typeName for validation
  def typeName(): String = {
    this match {
      case p if p.trail.nonEmpty => p.trail.get.typeName()
      case _ => typeId
    }
  }
}

sealed trait DtResult {}
final case class DtOk() extends DtResult
final case class DtErr(message: String) extends DtResult

// particular type of a kind
final case class DtType(
    // the name of our type
    name: String,
    // the names of the properties (called props instead of attributes because
    // values of props can change - values of attributes cannot change)
    props: List[String],
    // datetime of creation - no updates allowed
    created: ZonedDateTime = ZonedDateTime.now()
)

final case class LazyTelemetry (
    idx: Int,
    value: Double
) {
  def telemetry(): Telemetry = Telemetry(idx, value)
}

final case class Telemetry (
    idx: Int,
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
    case _ => this
  }
}

sealed trait DtMsg[T] {
  def path(): DtPath
  def content(): T
  def trailMsg(): DtMsg[T]
}

final case class TelemetryMsg(p: DtPath, c: Telemetry)
    extends DtMsg[Telemetry] {
  def path(): DtPath = p
  def content(): Telemetry = c
  def trailMsg(): TelemetryMsg = p.trail match {
    case Some(tp) =>
      TelemetryMsg(tp, c)
    case _ => this
  }
}
