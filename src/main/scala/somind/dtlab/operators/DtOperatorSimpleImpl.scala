package somind.dtlab.operators

import java.time.ZonedDateTime

import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models.{DtState, Operator, Telemetry}

import scala.concurrent.duration.TimeUnit

trait DtOperatorSimpleImpl extends DtOperatorImpl with LazyLogging {

  def activeDuration(telemetry: Telemetry,
                     dtState: DtState,
                     op: Operator,
                     unit: TimeUnit): Option[Telemetry] = {

    // warning - do not run a nuclear power plant or a jetliner based on this calculation
    val prevState: Option[Telemetry] = dtState.state.get(op.output)
    val prevDate: ZonedDateTime = prevState
      .map(_.datetime.getOrElse(ZonedDateTime.now()))
      .getOrElse(ZonedDateTime.now())
    val lastCount = prevState.map(_.value).getOrElse(0.0)
    val newCount = unit.toChronoUnit
      .between(prevDate, telemetry.datetime.getOrElse(ZonedDateTime.now())) + lastCount
    if (newCount > lastCount || prevState.isEmpty) {
      val t = Telemetry(op.output, newCount)
      Some(t)
    } else
      None

  }

  def applyImplementation(telemetry: Telemetry,
                          dtState: DtState,
                          op: Operator): Option[Telemetry]

  // entry point
  override def apply(telemetry: Telemetry,
                     dtState: DtState,
                     op: Operator): Option[Telemetry] = {
    op match {

      case _ if op.input.isEmpty =>
        logger.warn(s"operator '$op' does not specify input")
        None

      case _ if op.output < 0 =>
        logger.warn(s"operator '$op' does not specify a valid idx")
        None

      case _ if !op.input.contains(telemetry.idx) =>
        // just a sanity check - this should never happen - runtime uses op to select telemetry
        logger.warn(
          s"telemetry $telemetry does not match specified input for op '$op'")
        None

      case _ =>
        logger.debug(s"applying operator ${op.name} for state idx ${telemetry.idx}")
        applyImplementation(telemetry, dtState, op)

    }
  }
}
