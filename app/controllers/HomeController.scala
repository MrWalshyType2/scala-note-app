package controllers

import api.notes.{NoteController, NoteResourceHandler}
import javax.inject._
import play.api._
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, text}
import play.api.mvc._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               val handler: NoteResourceHandler, val noteController: NoteController) extends BaseController with play.api.i18n.I18nSupport {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    val notesFuture = handler.getList
    val notes = Await.result(notesFuture, Duration.Inf)
    Ok(views.html.index(notes))
  }

  def create() = Action { implicit request: Request[AnyContent] =>
    val postUrl = Call.apply("POST", "http://localhost:9000/notes")
    Ok(views.html.createNote(noteController.form, postUrl))
  }

  def update() = Action { implicit request: Request[AnyContent] =>
    val putUrl = Call.apply("POST", "http://localhost:9000/notes/update")
    Ok(views.html.updateNote(noteController.updateForm, putUrl))
  }

  def delete() = Action { implicit request: Request[AnyContent] =>
    val delUrl = Call.apply("POST", "http://localhost:9000/notes/delete")
    Ok(views.html.deleteNote(noteController.deleteForm, delUrl))
  }
}