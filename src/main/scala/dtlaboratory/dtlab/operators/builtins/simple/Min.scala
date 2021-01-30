package dtlaboratory.dtlab.operators.builtins.simple

import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.models.{DtState, Operator, Telemetry}
import dtlaboratory.dtlab.operators.DtOperatorSimpleImpl

/**
 * keep track of the min value ever seen and store it in a dedicated field
 */
object Min extends DtOperatorSimpleImpl with LazyLogging {

  override def applyImplementation(telemetry: Telemetry,
                                   dtState: DtState,
                                   op: Operator): List[Telemetry] = {

    val previousMin =
      dtState.state
        .get(op.output.head)
        .map(_.value)
        .getOrElse(Int.MaxValue.toDouble)

    if (telemetry.value < previousMin) {
      val output = Telemetry(op.output.head, telemetry.value)
      List(output)
    } else
      List()

  }
}
