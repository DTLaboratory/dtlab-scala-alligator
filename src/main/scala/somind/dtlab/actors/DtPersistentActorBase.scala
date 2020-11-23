package somind.dtlab.actors

import akka.persistence.PersistentActor
import somind.dtlab.Conf._
import somind.dtlab.models.DtStateHolder
import somind.dtlab.observe.Observer

// Dt Persistent Actor Base
abstract class DtPersistentActorBase[T]
    extends DtActorBase
    with PersistentActor {

  var state: T

  override def persistenceId: String =
    persistIdRoot + "_" + self.path.toString.replace('-', '_')

  def takeSnapshot(now: Boolean = false): Unit = {
    if (now || lastSequenceNr % snapshotInterval == 0 && lastSequenceNr != 0) {
      saveSnapshot(DtStateHolder[T](state, children))
      Observer("actor_saved_state_snapshot")
    }
  }

}
