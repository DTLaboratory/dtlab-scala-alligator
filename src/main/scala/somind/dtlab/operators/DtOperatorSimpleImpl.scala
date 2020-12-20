package somind.dtlab.operators

import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models.{DtState, Operator, Telemetry}

trait DtOperatorSimpleImpl extends DtOperatorImpl with LazyLogging {

  def applyImplementation(telemetry: Telemetry,
                          dtState: DtState,
                          op: Operator): Option[Telemetry]

  override def apply(telemetry: Telemetry,
                     dtState: DtState,
                     op: Operator): Option[Telemetry] = {
    op match {

      case _ if op.input.isEmpty =>
        logger.warn(s"operator '$op' does not specify input")
        None

      case _ if op.output < 0=>
        logger.warn(s"operator '$op' does not specify a valid idx")
        None

      case _ if !op.input.contains(telemetry.idx) =>
        // just a sanity check - this should never happen - runtime uses op to select telemetry
        logger.warn(
          s"telemetry $telemetry does not match specified input for op '$op'")
        None

      case _ => applyImplementation(telemetry, dtState, op)

    }
  }
}
