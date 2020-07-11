package somind.dtlab.actors

import akka.actor.Actor
import akka.persistence.PersistentActor
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.Conf._

abstract class DtLabActor[T]
    extends Actor
    with PersistentActor
    with LazyLogging {

  var state: T

  override def persistenceId: String =
    persistIdRoot + "_" + self.path.toString.replace('-', '_')

  def takeSnapshot(): Unit = {
    if (lastSequenceNr % snapshotInterval == 0 && lastSequenceNr != 0) {
      saveSnapshot(state)
    }
  }

}
