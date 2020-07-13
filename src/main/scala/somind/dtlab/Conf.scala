package somind.dtlab

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.actors._
import somind.dtlab.observe.Observer

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.{Duration, FiniteDuration}

object Conf extends LazyLogging {

  implicit val system: ActorSystem = ActorSystem("DtLab-system")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val conf: Config = ConfigFactory.load()

  val healthToleranceSeconds: Int = conf.getString("main.healthToleranceSeconds").toInt

  def requestDuration: Duration = {
    val t = "120 seconds"
    Duration(t)
  }
  implicit def requestTimeout: Timeout = {
    val d = requestDuration
    FiniteDuration(d.length, d.unit)
  }

  val observer: ActorRef = system.actorOf(Props[Observer], "observer")

  val dtDirectory: ActorRef = system.actorOf(Props[DtDirectory], "dtDirectory")

  val persistIdRoot: String = conf.getString("main.persistIdRoot")
  val snapshotInterval: Int = conf.getInt("main.snapshotInterval")

}
