package api.notes

import javax.inject.{Inject, Provider, Singleton}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}

@Singleton
class IdGenerator @Inject()(handler: Provider[NoteResourceHandler]) {

  private def max(id1: Int, id2: Int): Int = {
    if (id1 > id2) id1 else id2
  }

  def generate: NoteId = {
    val listOfNotes = Await.result(handler.get.getList, Duration.Inf)
    val id = Try(listOfNotes.map(noteResource => noteResource.id.toInt).reduceLeft(max))

    id match {
      case Success(value) => NoteId((id.get + 1).toString)
      case Failure(exception) => NoteId("1")
    }
  }
}
