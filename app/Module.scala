import api.notes._
import com.google.inject.AbstractModule
import javax.inject._
import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment}

class Module(environment: Environment, configuration: Configuration)
  extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    bind[NoteRepository].to[NoteRepositoryImplementation].in[Singleton]
  }
}
