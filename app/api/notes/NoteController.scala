package api.notes

import javax.inject.Inject
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}

case class NoteFormInput(title: String, body: String)
case class UpdateNoteFormInput(id: String, title: String, body: String)
case class DeleteFormInput(id: String)

class NoteController @Inject()(controllerComponents: NoteControllerComponents)(implicit ec: ExecutionContext)
  extends NoteBaseController(controllerComponents) {

  private val logger = Logger(getClass)

  val form: Form[NoteFormInput] = {
    Form(mapping(
      "title" -> nonEmptyText,
      "body" -> text
    )(NoteFormInput.apply)(NoteFormInput.unapply))
  }

  private val updateForm: Form[UpdateNoteFormInput] = {
    Form(mapping(
      "id" -> nonEmptyText,
      "title" -> nonEmptyText,
      "body" -> nonEmptyText
    )(UpdateNoteFormInput.apply)(UpdateNoteFormInput.unapply))
  }

  private val deleteForm: Form[DeleteFormInput] = {
    Form(mapping(
      "id" -> nonEmptyText
    )(DeleteFormInput.apply)(DeleteFormInput.unapply))
  }

  def get(id: String): Action[AnyContent] = NoteAction.async {
    implicit request =>
      logger.trace(s"GET: ID = $id")
      noteResourceHandler.get(id).map { note =>
        Ok(Json.toJson(note))
      }
  }

  def getAll: Action[AnyContent] = NoteAction.async {
    implicit request =>
      logger.trace("GET ALL:")
      noteResourceHandler.getList.map { notes =>
        Ok(Json.toJson(notes))
      }
  }

  def create: Action[AnyContent] = NoteAction.async {
    implicit request =>
      form.bindFromRequest.fold(
        formWithErrors => {
          Future(BadRequest(Json.toJson("Bad request")))
        },
        note => {
          logger.trace(s"CREATE: TITLE = ${note.title},")
          noteResourceHandler.create(note).map { note =>
            Ok(Json.toJson(note))
          }
        }
      )
  }

  def update: Action[AnyContent] = NoteAction.async {
    implicit request =>
      updateForm.bindFromRequest.fold(
        formWithErrors => {
          Future(BadRequest(Json.toJson("Bad request")))
        },
        note => {
          noteResourceHandler.update(note).map { note =>
            Ok(Json.toJson(note))
          }
        }
      )
  }

  def delete: Action[AnyContent] = NoteAction.async {
    implicit request =>
      deleteForm.bindFromRequest.fold(
        formWithErrors => {
          Future(BadRequest(Json.toJson("Bad request")))
        },
        noteId => {
          noteResourceHandler.delete(noteId.id)
          Future(Ok(Json.toJson("Success")))
        }
      )
  }
}
