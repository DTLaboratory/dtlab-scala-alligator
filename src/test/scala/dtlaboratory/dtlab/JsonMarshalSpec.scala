package dtlaboratory.dtlab

import dtlaboratory.dtlab.models.{
  DtEventMsg,
  JsonSupport,
  StateChange,
  StateChangeEventType,
  Telemetry
}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers._
import spray.json._

class JsonMarshalSpec
    extends AnyFlatSpec
    with should.Matchers
    with JsonSupport {

  "A value" should "marshal" in {

    val state = List(
      Telemetry(idx = 0, value = 0.1),
      Telemetry(idx = 1, value = 0.2)
    )

    val stateChangedEvent = StateChange(newState = state)

    val s1: String = stateChangedEvent.toJson.prettyPrint
    s1.toCharArray should contain('{')
    s1.toCharArray should contain('}')
    s1.toCharArray should contain('\n')
    val s2: String = stateChangedEvent.toJson.compactPrint
    s2.toCharArray should contain('{')
    s2.toCharArray should contain('}')
    s2.toCharArray should not contain '\n'

    val json: JsValue = s1.parseJson
    val resurrected = json.convertTo[StateChange]
    resurrected.newState.length should be(2)

    val segs = List("mytype1", "t1", "mysubtype1", "s1")
    dtlaboratory.dtlab.models.DtPath(segs) match {
      case Some(dtPath) =>
        val msg: DtEventMsg = DtEventMsg(event = resurrected,
                                         source = dtPath,
                                         eventType = StateChangeEventType())
        val s3: String = msg.toJson.prettyPrint
        s3.toCharArray should contain('{')
        s3.toCharArray should contain('}')
        s3.toCharArray should contain('\n')
      case _ =>
        fail()
    }

  }

}
