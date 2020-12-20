package somind.dtlab.operators

import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models.{DtOperatorImpl, DtState, Operator, Telemetry}

object ApplyBuiltInOperator extends DtOperatorImpl with LazyLogging {

  override def apply(telemetry: Telemetry,
                     dtState: DtState,
                     op: Operator,
                     updateFun: Telemetry => Unit): Unit = {
    op.implementation match {
      case "count" =>
        Count(telemetry, dtState, op, updateFun)
      case op =>
        logger.warn(s"can not find built-in operator '$op'")
    }
  }

}
