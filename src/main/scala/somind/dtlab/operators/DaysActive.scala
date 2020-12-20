package somind.dtlab.operators

import java.time.ZonedDateTime

import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.models._

import scala.concurrent.duration.{DAYS, TimeUnit}

/**
  * input is a particular state field - monitor any telemetry for that slot
  * output incremented forever - so real input is output :)
  */
object DaysActive extends DtOperatorSimpleImpl with LazyLogging {


  override def applyImplementation(telemetry: Telemetry,
                                   dtState: DtState,
                                   op: Operator): Option[Telemetry] = {

    activeDuration(telemetry, dtState, op, DAYS)

  }

}
