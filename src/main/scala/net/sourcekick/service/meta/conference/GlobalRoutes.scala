package net.sourcekick.service.meta.conference

import javax.ws.rs.{Path}

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive0, Route}
import akka.stream.ActorMaterializer
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import org.slf4j.{Logger, LoggerFactory}
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers._

import scala.concurrent.ExecutionContext

/**
  * This class provides the global routing entry point for the service.
  */
@io.swagger.annotations.Api(
  produces = "application/json",
  tags = Array("assets", "points"),
  authorizations = Array(
    new io.swagger.annotations.Authorization(
      value = "Authorization"
    )
  )
)
@Path("api/v1")
class GlobalRoutes(private val dispatcher: ExecutionContext,
                   private val materializer: ActorMaterializer,
                   private val conferenceRepository: ConferenceRepository) {

  private final val log: Logger = LoggerFactory.getLogger(this.getClass)

  final val REALM = "Authorization"

  /* TODO priority 3 make this work
  final val ApiInfo: Map[String, String] = Map(
    "name"    -> BuildInfo.name,
    "version" -> BuildInfo.version
  )
   */

  val conferenceRoutes = new ConferenceRoutes(dispatcher, materializer, conferenceRepository)

  def entryPoint: Route = pathPrefix("api" / "v1") {
    extractRequest { request =>
      log.debug("Received request={}", request)
      addAccessControlHeaders {
        apiInfo ~ preflightRequestHandler ~ conferenceRoutes.loadConference ~ conferenceRoutes.createConference ~ conferenceRoutes.updateConference ~ conferenceRoutes.removeConference
      }
    //apiInfo ~ conferenceRoutes.loadConference ~ conferenceRoutes.createConference ~ conferenceRoutes.updateConference ~ conferenceRoutes.removeConference
    }
  }

  @io.swagger.annotations.ApiOperation(
    response = classOf[scala.collection.immutable.Map[String, String]],
    value = "Return service version information."
  )
  @javax.ws.rs.GET
  @Path("info")
  def apiInfo: Route = path("info") {
    complete("Here is supposed to be the meta-conference API Info.")
    // complete(ApiInfo) TODO priority 3 make this work
  }

  //this directive adds access control headers to normal responses
  private def addAccessControlHeaders: Directive0 = {
    respondWithHeaders(
      `Access-Control-Allow-Origin`.*,
      `Access-Control-Allow-Credentials`(true),
      `Access-Control-Allow-Headers`("Authorization", "Content-Type", "X-Requested-With"),
      `Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE)
    )
  }

  //this handles preflight OPTIONS requests.
  private def preflightRequestHandler: Route = options {
    complete(HttpResponse(StatusCodes.OK))
  }

}
