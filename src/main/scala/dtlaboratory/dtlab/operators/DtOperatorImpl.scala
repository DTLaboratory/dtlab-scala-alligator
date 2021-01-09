package dtlaboratory.dtlab.operators

import com.typesafe.scalalogging.LazyLogging
import dtlaboratory.dtlab.models.{DtState, Operator, Telemetry}

trait DtOperatorImpl extends LazyLogging {

  def apply(telemetry: Telemetry,
            dtState: DtState,
            op: Operator): List[Telemetry]

}
