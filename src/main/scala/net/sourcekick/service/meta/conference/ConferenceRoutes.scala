package net.sourcekick.service.meta.conference

import javax.ws.rs.Path

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import io.swagger.annotations.{Api, Authorization}

import scala.concurrent.ExecutionContext

@Api(produces = "application/json",
     tags = Array("conferences"),
     authorizations = Array(
       new Authorization(
         value = "Authorization"
       )
     ))
@Path("api/v1")
class ConferenceRoutes(private val dispatcher: ExecutionContext, private val materializer: ActorMaterializer) {

  // Our custom dispatcher needs to be in implicit scope.
  //private implicit val ec: ExecutionContext = dispatcher

  // Our custom materializer needs to be in implicit scope.
  //private implicit val mat: ActorMaterializer = materializer

  def createConference: Route = pathPrefix("conferences") {
    ???
    // TODO priority 1 define inner route
  }

  def loadConference: Route = pathPrefix("conferences") {
    ???
    // TODO priority 1 define inner route
  }

  def updateConference: Route = pathPrefix("conferences") {
    ???
    // TODO priority 1 define inner route
  }

  def removeConference: Route = pathPrefix("conferences") {
    ???
    // TODO priority 1 define inner route
  }

}
