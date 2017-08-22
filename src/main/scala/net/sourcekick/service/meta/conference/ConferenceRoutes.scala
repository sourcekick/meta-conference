package net.sourcekick.service.meta.conference

import javax.ws.rs.Path

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import io.swagger.annotations.{Api, Authorization}
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import io.circe.java8.time._
import io.circe.generic.auto._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

@Api(produces = "application/json",
     tags = Array("conferences"),
     authorizations = Array(
       new Authorization(
         value = "Authorization"
       )
     ))
@Path("api/v1")
class ConferenceRoutes(private val dispatcher: ExecutionContext,
                       private val materializer: ActorMaterializer,
                       private val conferenceRepository: ConferenceRepository) {

  // Our custom dispatcher needs to be in implicit scope.
  private implicit val ec: ExecutionContext = dispatcher

  // Our custom materializer needs to be in implicit scope.
  //private implicit val mat: ActorMaterializer = materializer

  val uuidRegex = """[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}""".r

  def createConference: Route = pathPrefix("conferences") {
    pathPrefix(uuidRegex) { uuid =>
      pathEnd {
        post {
          entity(as[Conference]) { conference =>
            // complete(conferenceRepository.createConference(conference))

            onComplete(conferenceRepository.createConference(conference)) {
              case Success(item)  => complete((StatusCodes.Created, item))
              case f @ Failure(_) => reject
            }
          }
        }
      }
    }
  }

  def loadConference: Route = pathPrefix("conferences") {
    pathPrefix(uuidRegex) { uuid =>
      pathEnd {
        get {
          complete(conferenceRepository.loadConference(uuid))
        }
      }
    }
  }

  def updateConference: Route = pathPrefix("conferences") {
    pathPrefix(uuidRegex) { uuid =>
      pathEnd {
        put {
          entity(as[Conference]) { conference =>
            complete(conferenceRepository.updateConference(conference))
          }
        }
      }
    }
  }

  def removeConference: Route = pathPrefix("conferences") {
    pathPrefix(uuidRegex) { uuid =>
      pathEnd {
        delete {
          complete(conferenceRepository.removeConference(uuid))
        }
      }
    }
  }

}
