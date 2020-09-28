package controllers

import play.libs.Json

// DTO
case class NoteResource(id: String, title: String, body: String)

object NoteResource {
  // Read/Write a NoteResource as a JSON value
  implicit val format: Format[NoteResource] = Json
}

class NoteResourceHandler {

}
