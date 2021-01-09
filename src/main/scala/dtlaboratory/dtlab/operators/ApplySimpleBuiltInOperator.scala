package dtlaboratory.dtlab.operators

import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.models.{DtState, Operator, Telemetry}
import dtlaboratory.dtlab.operators.builtins.simple._

/**
 * these built-ins should run before any complex operators that might want some of this info
 */
object ApplySimpleBuiltInOperator extends DtOperatorImpl with LazyLogging {

  override def apply(telemetry: Telemetry,
                     dtState: DtState,
                     op: Operator): List[Telemetry] = {
    op.implementation.toLowerCase() match {
      case "count" =>
        Count(telemetry, dtState, op)
      case "max" =>
        Max(telemetry, dtState, op)
      case "min" =>
        Min(telemetry, dtState, op)
      case "minutes_active" =>
        MinutesActive(telemetry, dtState, op)
      case "hours_active" =>
        HoursActive(telemetry, dtState, op)
      case "days_active" =>
        DaysActive(telemetry, dtState, op)
      case _ =>
        List()

    }
  }

}
