package dtlaboratory.dtlab.operators.builtins.simple

import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.models.{DtState, Operator, Telemetry}
import dtlaboratory.dtlab.operators.DtOperatorSimpleImpl

import scala.concurrent.duration.MINUTES

/**
 * input is a particular state field - monitor any telemetry for that slot
 * output incremented forever - so real input is output :)
 */
object MinutesActive extends DtOperatorSimpleImpl with LazyLogging {

  /**
   *
   * @param telemetry triggers the invocation
   * @param dtState prev state before all operators act on this telemetry
   * @param op the op needs to specify input even though it does not process
   *           it - the input is just the trigger for invocation
   * @return
   */
  override def applyImplementation(telemetry: Telemetry,
                                   dtState: DtState,
                                   op: Operator): List[Telemetry] = {

    activeDuration(telemetry, dtState, op, MINUTES)

  }

}
