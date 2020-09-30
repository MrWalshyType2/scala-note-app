package api.notes

import javax.inject.{Inject, Provider}
import play.api.MarkerContext
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

// DTO
case class NoteResource(id: String, title: String, body: String)

object NoteResource {
  // Read/Write a NoteResource as a JSON value
  implicit val format: Format[NoteResource] = Json.format
}

// Controls data access, returns a [NoteResource]
class NoteResourceHandler @Inject()(
   routerProvider: Provider[NoteRouter],
   noteRepository: NoteRepository)(implicit ec: ExecutionContext) {

  private def createNoteResource(n: NoteData): NoteResource = {
    NoteResource(n.id.toString, n.title, n.body)
  }

  def create(noteInput: NoteFormInput)(implicit mc: MarkerContext): Future[Option[NoteResource]] = {
    // Generate ID, check if in db already before creating

    val data = NoteData(NoteId(s"${Random.nextInt()}"), noteInput.title, noteInput.body)

    noteRepository.create(data).map { result =>
      result.map { maybeNoteData =>
        createNoteResource(maybeNoteData)
      }
    }
  }

  def get(id: String)(implicit mc: MarkerContext): Future[Option[NoteResource]] = {
    val noteFuture = noteRepository.get(NoteId(id))
    noteFuture.map { maybeNoteData =>
      maybeNoteData.map { noteData =>
        createNoteResource(noteData)
      }
    }
  }

  def getList(implicit mc: MarkerContext): Future[Iterable[NoteResource]] = {
    noteRepository.listOfNotes().map { noteDataList =>
      noteDataList.map(noteData => createNoteResource(noteData))
    }
  }

  def update(noteInput: UpdateNoteFormInput): Future[Option[NoteResource]] = {
    val data = NoteData(NoteId(noteInput.id), noteInput.title, noteInput.body)

    noteRepository.update(data)
  }
}
