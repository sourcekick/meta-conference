package net.sourcekick.service.meta.conference

import javax.ws.rs.{GET, Path}

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import io.swagger.annotations._
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext

/**
  * This class provides the global routing entry point for the service.
  */
@Api(
  produces = "application/json",
  tags = Array("assets", "points"),
  authorizations = Array(
    new Authorization(
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
      apiInfo ~ conferenceRoutes.loadConference ~ conferenceRoutes.removeConference
    }
  }

  @ApiOperation(
    response = classOf[scala.collection.immutable.Map[String, String]],
    value = "Return service version information."
  )
  @GET
  @Path("info")
  def apiInfo: Route = path("info") {
    complete("Here is supposed to be the meta-conference API Info.")
    // complete(ApiInfo) TODO priority 3 make this work
  }

}
