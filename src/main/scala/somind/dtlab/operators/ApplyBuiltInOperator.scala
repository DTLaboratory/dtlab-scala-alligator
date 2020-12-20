package somind.dtlab.operators

import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models.{DtState, Operator, Telemetry}

object ApplyBuiltInOperator extends DtOperatorImpl with LazyLogging {

  override def apply(telemetry: Telemetry,
                     dtState: DtState,
                     op: Operator): Option[Telemetry] = {
    op.implementation.toLowerCase() match {
      case "count" =>
        Count(telemetry, dtState, op)
      case "max" =>
        Max(telemetry, dtState, op)
      case "min" =>
        Min(telemetry, dtState, op)
      case op =>
        logger.warn(s"can not find built-in operator '$op'")
        None
    }
  }

}
