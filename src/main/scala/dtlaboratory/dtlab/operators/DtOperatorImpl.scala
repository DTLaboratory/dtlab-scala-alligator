package dtlaboratory.dtlab.operators

import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.models.{DtState, Operator, Telemetry}

/**
  * make your built-in operators adhere to this interface.
  *
  */
trait DtOperatorImpl extends LazyLogging {

  /**
   *
   * @param telemetry * is the trigger - latest input
   * @param dtState is the state BEFORE any operators are applied to the telemetry
   * @param op holds the parameters you are expected to respect and your inputs and outputs by index
   * @return the state updates to be applied once all operators have executed
   */
  def apply(telemetry: Telemetry,
            dtState: DtState,
            op: Operator): List[Telemetry]

}
