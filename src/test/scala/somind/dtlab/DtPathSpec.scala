package somind.dtlab

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import somind.dtlab.models.{DtPath, DtState, Telemetry}

class DtPathSpec extends AnyFlatSpec with should.Matchers {

  "A dtpath" should "parse" in {

    val segs = List("mytype1", "t1", "mysubtype1", "s1")

    val dtpo = DtPath(segs)
    dtpo should be('defined)
    dtpo match {
      case Some(dtp) =>
        dtp.instanceId should be("t1")
        dtp.typeId should be("mytype1")
        dtp.trail should be('defined)
        dtp.trail match {
          case Some(trailDtp) =>
            trailDtp.typeId should be("mysubtype1")
            trailDtp.instanceId should be("s1")
          case _ =>
            fail()
        }
      case _ =>
        fail()
    }

  }

  "A state" should "expand" in {
    val oldState = DtState(Map(0 -> Telemetry(0, 1.0), 1 -> Telemetry(1, 2.0)))

    val t3 = Telemetry(1, 3.0)
    val newState = oldState.state.updated(t3.idx, t3)

    newState(1).idx should be(1)

    val t4 = Telemetry(2, 4.0)
    val newerState = oldState.state.updated(t4.idx, t4)
    newerState(2).idx should be(2)
  }

  "A path" should "validate" in {

    DtPath(List("mytype1", "t1", "mysubtype1", "s1", "mysubtype2", "ss2")) match {
      case Some(dtp) =>
        val r = dtp.relationships()
        val (parent, child) = r.head
        parent should be("mytype1")
        child should be("mysubtype1")
        val (parent2, child2) = r(1)
        parent2 should be("mysubtype1")
        child2 should be("mysubtype2")
      case _ =>
        fail()
    }

  }

}
