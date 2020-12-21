package somind.dtlab.operators

import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models.{DtState, Operator, Telemetry}

/**
 * these built-ins should run before any complex operators that might want some of this info
 */
object ApplyComplexBuiltInOperator extends DtOperatorImpl with LazyLogging {

  override def apply(telemetry: Telemetry,
                     dtState: DtState,
                     op: Operator): List[Telemetry] = {
    op.implementation.toLowerCase() match {
      case _ =>
        List()

    }
  }

}
