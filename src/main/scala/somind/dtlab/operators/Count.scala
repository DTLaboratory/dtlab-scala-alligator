package somind.dtlab.operators

import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models._

/**
  * input is a particular state field - monitor any telemetry for that slot
  * output incremented forever - so real input is output :)
  */
object Count extends DtOperatorImpl with LazyLogging {

  override def apply(telemetry: Telemetry,
                     dtState: DtState,
                     op: Operator,
                     updateFun: Telemetry => Unit): Unit = {

    if (op.input.isEmpty || op.output.isEmpty) {
      logger.warn(s"operator '$op' does not have both an input and an output")
      return
    }

    val oldCount: Double =
      dtState.state.get(op.output.head).map(_.value).getOrElse(0)

    val t = Telemetry(op.output.head, oldCount + 1)

    updateFun(t)

  }
}
