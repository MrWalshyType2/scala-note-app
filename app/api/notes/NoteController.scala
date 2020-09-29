package api.notes

import javax.inject.Inject
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

case class NoteFormInput(title: String, body: String)

class NoteController @Inject()(controllerComponents: NoteControllerComponents)(implicit ec: ExecutionContext)
  extends NoteBaseController(controllerComponents) {

  private val logger = Logger(getClass)

  private val form: Form[NoteFormInput] = {
    import play.api.data.Forms._

    Form(mapping(
      "title" -> nonEmptyText,
      "body" -> text
    )(NoteFormInput.apply)(NoteFormInput.unapply))
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


}
