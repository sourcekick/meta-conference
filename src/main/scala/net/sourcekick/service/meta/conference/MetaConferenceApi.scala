package net.sourcekick.service.meta.conference

import akka.actor.Status.Failure
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.pipe
import akka.stream.ActorMaterializer
import com.typesafe.config.Config
import net.sourcekick.service.meta.conference.MetaConferenceApi.MetaConferenceApiMessages

/**
  * The actor that serves the API of the `meta-conference` system.
  */
class MetaConferenceApi(materializer: ActorMaterializer) extends Actor with ActorLogging {
  import context.dispatcher

  log.info("Starting meta-conference API actor.")

  // Default configuration
  private val config: Config = context.system.settings.config
  private val port: Int = config.getInt("meta-conference.api.port")
  private val address: String = config.getString("meta-conference.api.address")

  implicit val mat: ActorMaterializer = materializer
  implicit val system: ActorSystem = context.system

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  override def receive: Receive = {
    case MetaConferenceApiMessages.Boot =>
      // Swagger Service
      val swaggerDocService: SwaggerDocService = new SwaggerDocService(system, materializer)
      val swaggerRoutes: Route = swaggerDocService.routes
      // API routes.
      val apiRoutes: Route =
        new GlobalRoutes(system.dispatchers.lookup("meta-conference.api.routing-dispatcher"), materializer).entryPoint
      // Start server.
      val _ = Http(system).bindAndHandle(swaggerRoutes ~ apiRoutes, address, port).pipeTo(self)
      context.become(binding(sender()))
  }

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def binding(supervisor: ActorRef): Receive = {
    case Http.ServerBinding(socketAddress) =>
      log.info("Meta-Asset Api listening on {}.", socketAddress)
      supervisor ! MetaConferenceApiMessages.ApiReadyToServe
      context.become(Actor.emptyBehavior)

    case Failure(cause) =>
      log.error(cause, "Meta-Asset Api: Can't bind to {}:{}!", address, port)
      context.stop(self)

    case MetaConferenceApiMessages.Stop =>
      log.info("Received Stop message")
      context.stop(self)
  }
}

object MetaConferenceApi {

  /**
    * A factory method to create the actor.
    *
    * @param materializer The actor materializer that is necessary for akka http.
    * @return The props to create the actor
    */
  def props(materializer: ActorMaterializer): Props =
    Props(new MetaConferenceApi(materializer))

  /**
    * A sealed trait that contains the messages of the actor
    */
  sealed trait MetaConferenceApiMessages

  object MetaConferenceApiMessages {

    /**
      * A status notification that indicates that the api spun up by the
      * actor is ready to serve.
      */
    case object ApiReadyToServe extends MetaConferenceApiMessages

    /**
      * Tell the actor to initialise itself, e.g. load needed resources
      * and start the api server.
      */
    case object Boot extends MetaConferenceApiMessages

    /**
      * Tell the actor to stop.
      */
    case object Stop extends MetaConferenceApiMessages

  }
}
