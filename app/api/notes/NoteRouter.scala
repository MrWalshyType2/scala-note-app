package api.notes

import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class NoteRouter @Inject()(controller: NoteController) extends SimpleRouter {

  val prefix = "/notes"

  override def routes: Routes = {
    case GET(p"/") => controller.getAll

    case GET(p"/$id") => controller.get(id)

    case POST(p"/")  => controller.create

    case PUT(p"/") => controller.update

    case DELETE(p"/") => controller.delete
  }
}
