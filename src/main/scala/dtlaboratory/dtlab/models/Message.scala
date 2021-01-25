package dtlaboratory.dtlab.models

import java.time.ZonedDateTime
import akka.actor.ActorRef
import com.typesafe.scalalogging.LazyLogging

object DtPath extends LazyLogging {

  def applyActorRef(actorRef: ActorRef): Option[DtPath] =
    apply(actorRef.path.elements.slice(2, 999).toList)

  def apply(segs: List[String]): Option[DtPath] = {
    segs match {
      case s if s.length < 2 => None
      case s =>
        Option(
          DtPath(DtTypeName(s.head), DtInstanceName(s(1)), DtPath(s.drop(2))))
    }
  }
}

case class DtPath(typeId: DtTypeName,
                  instanceId: DtInstanceName,
                  trail: Option[DtPath] = None) {
  // convenience method to get the final typeName for validation
  def endTypeName(): DtTypeName = {
    trail match {
      case Some(t) =>
        t.endTypeName()
      case _ => typeId
    }
  }
  def relationships(): List[(String, String)] = {
    trail match {
      case None                              => List()
      case Some(dt) if typeId.name == "root" => dt.relationships()
      case Some(dt)                          => (typeId.name, dt.typeId.name) :: dt.relationships()
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

final case class DeleteDtType(typeId: DtTypeName)

final case class DtInstanceName(name: String) {
  override def toString: String = name
}

final case class DtTypeName(name: String) {
  override def toString: String = name
}

// particular type of a kind
final case class DtType(
    // the name of our type
    name: DtTypeName,
    // the names of the properties (called props instead of attributes because
    // values of props can change - values of attributes cannot change)
    props: Option[Seq[String]],
    // the ids/names of the types of child actors that this actor type can instantiate
    children: Option[Set[String]],
    // datetime of creation - no updates allowed
    created: ZonedDateTime
)

// for API to avoid setting created
final case class LazyDtType(
    props: Option[Seq[String]],
    children: Option[Set[String]]
) {
  def dtType(name: DtTypeName): DtType =
    DtType(name, props, children, ZonedDateTime.now())
}

// helper msg for internal dtpath-to-akka-ref info and for cloning via persistenceId
final case class ActorInfo(persistenceId: String, ref: ActorRef)

final case class Telemetry(
    idx: Int,
    value: Double,
    datetime: Option[ZonedDateTime] = Some(ZonedDateTime.now())
)

final case class NamedTelemetry(
    name: String,
    value: Double,
    datetime: Option[ZonedDateTime]
)

// collection of all props in an actor instance
final case class DtState(
    state: Map[Int, Telemetry] = Map()
)

// collection of all types in domain
final case class DtTypeMap(
    types: Map[DtTypeName, DtType]
)

final case class DtWebHookTarget(
    host: String, // ie: myexample.com
    port: Int, // ie: 443
    path: String, // /hit/me/here/12345-000-12345-1234567
    tls: Boolean, // causes webhook to be https: or http:
    clientCertificate: Option[String]
)

sealed trait DtEventType
final case class CreationEventType() extends DtEventType
final case class StateChangeEventType() extends DtEventType

sealed trait DtEvent
final case class Creation() extends DtEvent
final case class StateChange(newState: List[Telemetry]) extends DtEvent

final case class DtEventMsg(event: DtEvent,
                            eventType: DtEventType,
                            source: DtPath,
                            created: ZonedDateTime = ZonedDateTime.now())

final case class DtWebHook(
    name: Option[String] = None,
    target: DtWebHookTarget,
    dtPrefix: Option[DtPath],
    dtType: DtTypeName,
    eventType: DtEventType,
    created: Option[ZonedDateTime] = Some(ZonedDateTime.now())
)

// collection of all types in domain
final case class DtWebhookMap(
    webhooks: Map[String, DtWebHook]
)

final case class DeleteWebhook(name: String)

final case class Operator(name: String,
                          implementation: String,
                          params: Option[List[Double]],
                          input: List[Int],
                          output: List[Int],
                          created: Option[ZonedDateTime])
final case class OperatorMap(
    operators: Map[String, Operator] = Map()
)
final case class OperatorMsg(p: DtPath, c: Operator) extends DtMsg[Operator] {
  def path(): DtPath = p
  def content(): Operator = c
  def trailMsg(): DtMsg[Operator] = p.trail match {
    case Some(tp) =>
      OperatorMsg(tp, c)
    case _ => this
  }
}

// use to get an actor ref you can cache and bypass the DtPath routing overhead
// use to get an actor persistenceId if you are cloning an instance
final case class GetActorInfo(p: DtPath) extends DtMsg[Any] {
  override def path(): DtPath = p
  override def content(): Any = None
  def trailMsg(): GetActorInfo = p.trail match {
    case Some(tp) => GetActorInfo(tp)
    case _        => this
  }
}
final case class DeleteOperators(p: DtPath) extends DtMsg[Any] {
  override def path(): DtPath = p
  override def content(): Any = None
  def trailMsg(): DeleteOperators = p.trail match {
    case Some(tp) => DeleteOperators(tp)
    case _        => this
  }
}
final case class GetOperators(p: DtPath) extends DtMsg[Any] {
  override def path(): DtPath = p
  override def content(): Any = None
  def trailMsg(): GetOperators = p.trail match {
    case Some(tp) => GetOperators(tp)
    case _        => this
  }
}

final case class GetChildrenNames(p: DtPath) extends DtMsg[Any] {
  override def path(): DtPath = p
  override def content(): Any = None
  def trailMsg(): GetChildrenNames = p.trail match {
    case Some(tp) => GetChildrenNames(tp)
    case _        => this
  }
}

final case class GetJrnl(p: DtPath, limit: Int = 10, offset: Int = 0)
    extends DtMsg[(Int, Int)] {
  override def path(): DtPath = p
  override def content(): (Int, Int) = (limit, offset)
  def trailMsg(): GetJrnl = p.trail match {
    case Some(tp) => GetJrnl(tp, limit, offset)
    case _        => this
  }
}

final case class GetState(p: DtPath) extends DtMsg[Any] {
  override def path(): DtPath = p
  override def content(): Any = None
  def trailMsg(): GetState = p.trail match {
    case Some(tp) => GetState(tp)
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
    children: DtChildren,
    operators: OperatorMap = OperatorMap()
)
