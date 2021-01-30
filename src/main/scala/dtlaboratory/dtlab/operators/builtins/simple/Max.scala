package dtlaboratory.dtlab.operators.builtins.simple

import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.models.{DtState, Operator, Telemetry}
import dtlaboratory.dtlab.operators.DtOperatorSimpleImpl

/**
 * keep track of the max value ever seen and store it in a dedicated field
 */
object Max extends DtOperatorSimpleImpl with LazyLogging {

  override def applyImplementation(telemetry: Telemetry,
                                   dtState: DtState,
                                   op: Operator): List[Telemetry] = {

    val previousMax =
      dtState.state
        .get(op.output.head)
        .map(_.value)
        .getOrElse(Int.MinValue.toDouble)

    if (telemetry.value > previousMax) {
      val output = Telemetry(op.output.head, telemetry.value)
      List(output)
    } else
      List()

  }
}
