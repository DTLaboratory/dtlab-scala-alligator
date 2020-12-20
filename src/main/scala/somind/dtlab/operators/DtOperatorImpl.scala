package somind.dtlab.operators

import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models.{DtState, Operator, Telemetry}

trait DtOperatorImpl extends LazyLogging {

  def apply(telemetry: Telemetry,
            dtState: DtState,
            op: Operator): Option[Telemetry]

}
