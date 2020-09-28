package controllers

import javax.inject.Inject
import play.api.MarkerContext
import play.api.http.{FileMimeTypes, HttpVerbs}
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc.{ActionBuilder, AnyContent, BaseController, BodyParser, ControllerComponents, DefaultActionBuilder, MessagesRequestHeader, PlayBodyParsers, PreferredMessagesProvider, Request, Result, WrappedRequest}

import scala.concurrent.{ExecutionContext, Future}

// Request wrapper for holding request-specific information (credentials, shortcut methods...)
trait NoteRequestHeader extends MessagesRequestHeader with PreferredMessagesProvider
class NoteRequest[A](request: Request[A], val messagesApi: MessagesApi) extends WrappedRequest(request) with NoteRequestHeader

// Action builder is for: Logging, metrics, augmenting requests with context data, manipulating results
class NoteActionBuilder @Inject()(messagesApi: MessagesApi, playBodyParsers: PlayBodyParsers)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[NoteRequest, AnyContent]
  with HttpVerbs {

  type NoteRequestBlock[A] = NoteRequest[A] => Future[Result]

  override def parser: BodyParser[AnyContent] = playBodyParsers.anyContent

  override def invokeBlock[A](request: Request[A],
                              block: NoteRequestBlock[A]): Future[Result] = {
    val future = block(new NoteRequest(request, messagesApi))

    future.map { result =>
      request.method match {
        case GET | HEAD =>
          result.withHeaders("Cache-Control" -> s"max-age: 100")
        case other => result
      }
    }
  }
}

// Controller dependency injection
case class NoteControllerComponents @Inject()(
  noteActionBuilder: NoteActionBuilder,
  noteResourceHandler: NoteResourceHandler,
  actionBuilder: DefaultActionBuilder,
  parsers: PlayBodyParsers,
  messagesApi: MessagesApi,
  langs: Langs,
  fileMimeTypes: FileMimeTypes,
  executionContext: scala.concurrent.ExecutionContext) extends ControllerComponents

// Expose actions and handler to the NoteController
//  - Done by wiring the injected state into the base class
class NoteBaseController @Inject()(nc: NoteControllerComponents) extends BaseController {

  override protected def controllerComponents: ControllerComponents = nc

  def NoteAction: NoteActionBuilder = nc.noteActionBuilder

  def noteResourceHandler: NoteResourceHandler = nc.noteResourceHandler
}
