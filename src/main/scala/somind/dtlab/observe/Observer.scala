package somind.dtlab.observe

import akka.pattern.ask
import somind.dtlab.Conf._
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging

case class MeasurementsQuery()
case class FitnessQuery()

case class IncrementState(name: String, value: Long = 1)

sealed trait Observation {
  def value: Double
  def datetime: ZonedDateTime
}

case class Aggregate(name: String,
                     value: Double,
                     datetime: ZonedDateTime = ZonedDateTime.now())
    extends Observation {
  override def toString: String =
    s"# TYPE $name count\n" +
      s"# HELP $name The total count.\n" +
      s"$name $value"
}

case class Measurements(metrics: List[Observation])

sealed trait Fitness
case class Hardy() extends Fitness
case class Feeble() extends Fitness

object Observer {
  def props(): Props = Props(new Observer())
  def name = "observer"

  def apply(name: String): Unit = {
    observer ask IncrementState(name)
  }
}
class Observer extends Actor with LazyLogging {

  def calculateState(agingState: Map[String, Observation],
                     ob: IncrementState): Map[String, Observation] = {

    if (agingState.contains(ob.name))
      agingState + (ob.name -> Aggregate(ob.name,
                                         agingState(ob.name).value + ob.value))
    else
      agingState + (ob.name -> Aggregate(ob.name, ob.value))
  }

  var state: Map[String, Observation] = Map[String, Observation]()

  def seconds(d1: ZonedDateTime, d2: ZonedDateTime): Long =
    Math.abs(ChronoUnit.SECONDS.between(d1, d2))

  override def receive: Receive = {

    case m: IncrementState =>
      state = calculateState(state, m)

    case _: MeasurementsQuery =>
      // todo: repair names for k8s
      // val name = observation.name
      // .replaceAll("[^A-Za-z0-9_]", "")
      // .replace(".", "_")

      sender() ! Measurements(state.values.toList)

    case _: FitnessQuery =>
      val agingState = state
        .filter(_._1.contains("fitness"))
        .values
        .filter(m =>
          seconds(ZonedDateTime.now, m.datetime) > healthToleranceSeconds)
      if (agingState.nonEmpty) {
        sender() ! Feeble()
      } else {
        sender() ! Hardy()
      }

    case q =>
      logger.warn(s"I don't know how to handle $q")

  }

}
