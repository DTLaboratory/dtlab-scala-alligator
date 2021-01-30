package dtlaboratory.dtlab.operators

import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.models.{DtState, Operator, Telemetry}

/**
 * these built-ins should run before any complex operators that might want some of this info
 *
 * complex operators rely on a dependency graph mechanism still to be designed
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
