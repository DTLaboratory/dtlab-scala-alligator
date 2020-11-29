package somind.dtlab.actors

import akka.NotUsed
import akka.persistence.PersistentActor
import akka.persistence.jdbc.query.scaladsl.JdbcReadJournal
import akka.persistence.query.{EventEnvelope, PersistenceQuery}
import akka.stream.scaladsl.{Sink, Source}
import somind.dtlab.Conf._
import somind.dtlab.models.DtStateHolder
import somind.dtlab.observe.Observer

import scala.concurrent.Future

// Dt Persistent Actor Base
abstract class DtPersistentActorBase[T, J]
    extends DtActorBase
    with PersistentActor {

  var state: T

  def grabJrnl(limit: Int): Future[Seq[J]] = {

    logger.debug(s"${self.path} jrnl query limit $limit")
    // for debugging. reads all events every call
    val readJournal: JdbcReadJournal = PersistenceQuery(system)
      .readJournalFor[JdbcReadJournal](JdbcReadJournal.Identifier)
    val willCompleteTheStream: Source[EventEnvelope, NotUsed] =
      readJournal.currentEventsByPersistenceId(persistenceId, 0L, Long.MaxValue)

    val formattedResult: Source[J, NotUsed] =
      willCompleteTheStream.map((e: EventEnvelope) => {
        e.event.asInstanceOf[J]
      })
    formattedResult.runWith(Sink.seq[J]).map(_.reverse.slice(0, limit))
  }

  override def persistenceId: String =
    persistIdRoot + "_" + self.path.toString.replace('-', '_')

  def takeSnapshot(now: Boolean = false): Unit = {
    if (now || lastSequenceNr % snapshotInterval == 0 && lastSequenceNr != 0) {
      saveSnapshot(DtStateHolder[T](state, children))
      Observer("actor_saved_state_snapshot")
    }
  }

}
