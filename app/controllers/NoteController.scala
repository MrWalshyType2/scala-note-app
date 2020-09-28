package controllers

import javax.inject.Inject
import play.api.mvc.{BaseController, ControllerComponents}

import scala.concurrent.ExecutionContext

class NoteController @Inject()(controllerComponents: NoteControllerComponents)(implicit ec: ExecutionContext)
  extends BaseController {

  override protected def controllerComponents: ControllerComponents = ???
}
