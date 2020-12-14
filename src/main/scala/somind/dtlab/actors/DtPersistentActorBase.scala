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

  def grabJrnl(ilimit: Int, ioffset: Int): Future[Seq[J]] = {

    logger.debug(s"${self.path} jrnl query limit $ilimit and offset $ioffset")
    val (start: Long, stop: Long) = (ilimit.abs, ioffset.abs) match {
      case (limit, 0) =>
        val position = lastSequenceNr - limit + 1
        (position, Long.MaxValue)
      case (limit, offset) =>
        val position = lastSequenceNr - offset + 1
        (position, position + limit - 1)
    }
    val readJournal: JdbcReadJournal = PersistenceQuery(system)
      .readJournalFor[JdbcReadJournal](JdbcReadJournal.Identifier)
    val willCompleteTheStream: Source[EventEnvelope, NotUsed] =
      readJournal.currentEventsByPersistenceId(persistenceId, start, stop)

    val formattedResult: Source[J, NotUsed] =
      willCompleteTheStream.map((e: EventEnvelope) => {
        e.event.asInstanceOf[J]
      })
    formattedResult.runWith(Sink.seq[J]).map(_.reverse)
  }

  override def persistenceId: String =
    persistIdRoot + "_" + self.path.toString.replace('-', '_')

  def takeSnapshot(now: Boolean = false): Unit = {
    if (now || lastSequenceNr % snapshotInterval == 0 && lastSequenceNr != 0) {
      saveSnapshot(DtStateHolder[T](state, children, operators))
      Observer("actor_saved_state_snapshot")
    }
  }

}
