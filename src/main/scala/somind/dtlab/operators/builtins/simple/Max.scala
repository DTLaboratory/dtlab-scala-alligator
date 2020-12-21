package somind.dtlab.operators.builtins.simple

import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models.{DtState, Operator, Telemetry}
import somind.dtlab.operators.DtOperatorSimpleImpl

/**
 * input is a particular state field - monitor any telemetry for that slot
 * output incremented forever - so real input is both the t val and the output val
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
