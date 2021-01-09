package dtlaboratory.dtlab

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import dtlaboratory.dtlab.models._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.concurrent.Await
import scala.concurrent.duration._


class MyInstanceActor() extends Actor {
  override def receive: Receive = {
    case _ =>
      sender() forward self
  }
}

class MyTypeActor() extends Actor {
  val myInstanceActor: ActorRef = context.actorOf(Props[MyInstanceActor], "myinstance")
  override def receive: Receive = {
    case msg =>
      myInstanceActor forward msg
  }
}

class MyDtDirectory() extends Actor {
  val myTypeActor: ActorRef = context.actorOf(Props[MyTypeActor], "mytype")
  override def receive: Receive = {
    case msg =>
      myTypeActor forward  msg
  }
}

class DtPathFromActorRefSpec extends AnyFlatSpec with should.Matchers with JsonSupport {

  def requestDuration: Duration = {
    val t = "120 seconds"
    Duration(t)
  }
  implicit def requestTimeout: Timeout = {
    val d = requestDuration
    FiniteDuration(d.length, d.unit)
  }

  "A actorRef" should "parse" in {


    implicit val system: ActorSystem = ActorSystem("DtLab-unit-test")
    val s = system.actorOf(Props[MyDtDirectory](), "dtDirectory")

    val f = s ask "guh?"

    val myRef = Await.result(f, 3.second).asInstanceOf[ActorRef]

    val myDtPath = dtlaboratory.dtlab.models.DtPath.applyActorRef(myRef)

    myDtPath should be ('defined)
    myDtPath.get.toString should be ("/mytype/myinstance")

    val myEvent = DtEventMsg(Creation(), myDtPath.get)
    import spray.json._
    println(myEvent.toJson)

  }

}
