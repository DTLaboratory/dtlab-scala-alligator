package dtlaboratory.dtlab.operators.builtins.simple

import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.models.{DtState, Operator, Telemetry}
import dtlaboratory.dtlab.operators.DtOperatorSimpleImpl

/**
 * input is a particular state field - monitor any telemetry for that slot
 * output incremented forever - so real input is output :)
 */
object Count extends DtOperatorSimpleImpl with LazyLogging {

  override def applyImplementation(telemetry: Telemetry,
                                   dtState: DtState,
                                   op: Operator): List[Telemetry] = {
    val oldCount =
      dtState.state.get(op.output.head).map(_.value).getOrElse(0.0)
    val t = Telemetry(op.output.head, oldCount + 1)
    List(t)

  }

}
