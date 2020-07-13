package somind.dtlab

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import somind.dtlab.models.{DtPath, DtState, Telemetry}

class DtPathSpec extends AnyFlatSpec with should.Matchers {

  "A dtpath" should "parse" in {

    val segs = List("mytype1", "t1", "mysubtype1", "s1")

    DtPath(segs).get.instanceId should be("t1")
    DtPath(segs).get.typeId should be("mytype1")
    DtPath(segs).get.trail.get.typeId should be("mysubtype1")
    DtPath(segs).get.trail.get.instanceId should be("s1")
  }

  "A state" should "expand" in {

    val oldState = DtState(Map(0 -> Telemetry(0, 1.0), 1 -> Telemetry(1, 2.0)))

    val t3 = Telemetry(1, 3.0)
    val newState = oldState.state.updated(t3.idx, t3)

    println(newState)

    val t4 = Telemetry(2, 4.0)
    val newewState = oldState.state.updated(t4.idx, t4)

    println(newewState)
  }
}
