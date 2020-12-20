package somind.dtlab.operators

import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models._

/**
  * input is a particular state field - monitor any telemetry for that slot
  * output incremented forever - so real input is both the t val and the output val
  */
object Min extends DtOperatorSimpleImpl with LazyLogging {

  override def applyImplementation(telemetry: Telemetry,
                                   dtState: DtState,
                                   op: Operator): Option[Telemetry] = {

    val previousMin =
      dtState.state
        .get(op.output)
        .map(_.value)
        .getOrElse(Int.MaxValue.toDouble)

    if (telemetry.value < previousMin) {
      val output = Telemetry(op.output, telemetry.value)
      Some(output)
    } else
      None

  }
}
