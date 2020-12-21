package somind.dtlab.operators.builtins.simple

import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models.{DtState, Operator, Telemetry}
import somind.dtlab.operators.DtOperatorSimpleImpl

import scala.concurrent.duration.DAYS

/**
 * input is a particular state field - monitor any telemetry for that slot
 * output incremented forever - so real input is output :)
 */
object DaysActive extends DtOperatorSimpleImpl with LazyLogging {

  override def applyImplementation(telemetry: Telemetry,
                                   dtState: DtState,
                                   op: Operator): List[Telemetry] = {

    activeDuration(telemetry, dtState, op, DAYS)

  }

}
