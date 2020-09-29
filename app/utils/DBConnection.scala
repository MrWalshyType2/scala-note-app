package utils

import api.notes.{NoteData, NoteId}
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.bson.{BSONDocumentReader, BSONDocumentWriter, Macros}
import reactivemongo.api.{AsyncDriver, DB, MongoConnection}

import scala.concurrent.{ExecutionContext, Future}

object DBConnection {

  import ExecutionContext.Implicits.global

  val mongoURI = "mongodb://localhost:27017"

  val driver = new AsyncDriver()
  val parsedURI = MongoConnection.fromString(mongoURI)
  val connection = parsedURI.flatMap(driver.connect(_))

  def db: Future[DB] = connection.flatMap(_.database("play-notes"))

  def noteCollection: Future[BSONCollection] = db.map(_.collection("note"))

  implicit def noteWriter: BSONDocumentWriter[NoteData] = Macros.writer[NoteData]
  implicit def noteRead: BSONDocumentReader[NoteData] = Macros.reader[NoteData]

  implicit def noteIdWriter: BSONDocumentWriter[NoteId] = Macros.writer[NoteId]
  implicit def noteIdReader: BSONDocumentReader[NoteId] = Macros.reader[NoteId]
}
