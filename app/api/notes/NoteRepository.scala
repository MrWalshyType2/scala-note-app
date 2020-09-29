package api.notes

import java.util.Optional

import akka.actor.ActorSystem
import javax.inject.Inject
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}
import utils.DBConnection.{noteCollection, noteWriter}

import scala.concurrent.Future

final case class NoteData(id: NoteId, title: String, body: String)

class NoteId private (val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object NoteId {
  def apply(raw: String): NoteId = {
    // Tests an expression, blames the caller if false | IllegalArgumentException
    require(raw != null)
    new NoteId(Integer.parseInt(raw))
  }

  def unapply(noteId: NoteId): Option[String] = {
    require(noteId != null)
    Option(noteId.underlying.toString)
  }
}

// TODO: Look at this then when I know more about the basics of Play
class NoteExecutionContext @Inject()(actorSystem: ActorSystem)
  extends CustomExecutionContext(actorSystem, "repository.dispatcher")

// Non-blocking interface
trait NoteRepository {

  def create(data: NoteData)(implicit mc: MarkerContext): Future[NoteId]

  def listOfNotes()(implicit mc: MarkerContext): Future[Iterable[NoteData]]

  def get(id: NoteId)(implicit mc: MarkerContext): Future[Option[NoteData]]

  def update(): Future[AnyVal] = ??? // TODO:

  def delete(): Future[AnyVal] = ??? // TODO:
}

class NoteRepositoryImplementation @Inject()()(implicit ec: NoteExecutionContext)
  extends NoteRepository {

  private val logger = Logger(this.getClass)

  private val noteList = List(
    NoteData(NoteId("1"), "title 1", "note post 1"),
    NoteData(NoteId("2"), "title 2", "note post 2"),
    NoteData(NoteId("3"), "title 3", "note post 3"),
    NoteData(NoteId("4"), "title 4", "note post 4"),
    NoteData(NoteId("5"), "title 5", "note post 5")
  )

  override def create(data: NoteData)(implicit mc: MarkerContext): Future[NoteId] = {
    noteCollection.flatMap(_.insert.one(data).map(_ => {}))
    Future {
      logger.trace(s"CREATE NOTE: DATA = $data")
      data.id
    }
  }

  override def listOfNotes()(implicit mc: MarkerContext): Future[Iterable[NoteData]] = {
    Future {
      logger.trace(s"LIST OF NOTES: ")
      noteList
    }
  }

  override def get(id: NoteId)(implicit mc: MarkerContext): Future[Option[NoteData]] = {
    Future {
      logger.trace(s"GET NOTE: ID = $id")
      noteList.find(note => note.id == id)
    }
  }

}