package net.sourcekick.service.meta.conference

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, Terminated}
import akka.stream.ActorMaterializer
import net.sourcekick.service.meta.conference.MetaConferenceApi.MetaConferenceApiMessages.{ApiReadyToServe, Boot}
import net.sourcekick.service.meta.conference.MetaConferenceApiSupervisor.MetaConferenceApiSupervisorMessages.{
  ShutdownActorSystem,
  StartApi
}

import scala.concurrent.duration._

/**
  * The main actor for the service.
  *
  * @param materializer An actor materializer that is needed for akka http and akka streams.
  */
class MetaConferenceApiSupervisor(materializer: ActorMaterializer) extends Actor with ActorLogging {

  /**
    * Override the supervisory strategy to adapt it to our needs
    */
  override val supervisorStrategy: OneForOneStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1.minute) {
      case _ => Restart
    }

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  override def receive: Receive = {
    case ShutdownActorSystem =>
      log.info("Received shutdown command from {}.", sender().path)
      val _ = context.system.terminate()
      context.stop(self)

    case StartApi =>
      log.info("Receive StartApi message")
      val a: ActorRef = context.actorOf(MetaConferenceApi.props(materializer))
      val _ = context.watch(a)
      a ! Boot
      context.become(running(sender()))

  }

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def running(supervisor: ActorRef): Receive = {
    case ApiReadyToServe =>
      log.info("API actor informed supervisor that it is ready to serve.")

    case ShutdownActorSystem =>
      log.info("Received shutdown command from {}.", sender().path)
      val _ = context.system.terminate()
      context.stop(self)

    case Terminated(ref) =>
      log.info("Received termination message from {}.", ref.path)
  }

}

object MetaConferenceApiSupervisor {

  /**
    * A factory method to create the actor.
    *
    * @see [[MetaConferenceApiSupervisor]]
    * @param materializer An actor materializer that is needed for akka http and akka streams.
    * @return The props to create the actor.
    */
  def props(materializer: ActorMaterializer): Props =
    Props(new MetaConferenceApiSupervisor(materializer))

  /**
    * A sealed trait for the messages of the actor.
    */
  sealed trait MetaConferenceApiSupervisorMessages

  /**
    * A companion object for the trait to keep the namespace clean.
    */
  object MetaConferenceApiSupervisorMessages {

    /**
      * Indicates that the API has been started successfully.
      */
    case object ApiStarted extends MetaConferenceApiSupervisorMessages

    /**
      * Tell the actor to start the Api.
      */
    case object StartApi extends MetaConferenceApiSupervisorMessages

    /**
      * Tell the actor to shutdown the actor system.
      */
    case object ShutdownActorSystem extends MetaConferenceApiSupervisorMessages

  }

}
