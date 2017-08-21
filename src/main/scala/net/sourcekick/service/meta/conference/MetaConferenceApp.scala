package net.sourcekick.service.meta.conference

import akka.actor.{ActorRef, ActorSystem, Terminated}
import akka.stream.ActorMaterializer
import net.sourcekick.service.meta.conference.MetaConferenceApiSupervisor.MetaConferenceApiSupervisorMessages
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.Future

/**
  * Main entry point for the Meta Asset Service.
  */
object MetaConferenceApp {

  private final val log: Logger = LoggerFactory.getLogger(this.getClass)

  /**
    * Application main entry point.
    *
    * @param args Optional arguments.
    */
  def main(args: Array[String]): Unit = {
    // Needed for the `.onComplete` call of the future waiting for system termination.
    import scala.concurrent.ExecutionContext.Implicits.global

    log.info("Starting meta conference service.")

    /* TODO priority 3 make this work
    log.debug("Running version {}, compiled using {} at {}.",
      BuildInfo.version,
      BuildInfo.scalaVersion,
      BuildInfo.buildTime)
     */

    implicit val system: ActorSystem = ActorSystem("meta-conference")
    val materializer: ActorMaterializer = ActorMaterializer()

    val supervisor: ActorRef = system.actorOf(MetaConferenceApiSupervisor.props(materializer))
    supervisor ! MetaConferenceApiSupervisorMessages.StartApi

    val finish: Future[Terminated] = system.whenTerminated
    finish.onComplete(_ => log.info("Stopped meta conference service."))
  }

}
