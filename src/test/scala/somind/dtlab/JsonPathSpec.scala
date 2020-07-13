package somind.dtlab

import java.io.InputStream

import org.scalatest._
import matchers._

import scala.io.Source
import navicore.data.navipath.dsl.NaviPathSyntax._
import org.scalatest.flatspec.AnyFlatSpec

class JsonPathSpec extends AnyFlatSpec with should.Matchers {

  val stream: InputStream = getClass.getResourceAsStream("/my.json")
  val jsonString: String = Source.fromInputStream(stream).mkString

  "A value" should "match" in {

    val parsedJson = jsonString.asJson

    val r = parsedJson.query[Double]("$.myobject.mychildobject.myfield")

    r should be (Some(42.9))

  }

}
