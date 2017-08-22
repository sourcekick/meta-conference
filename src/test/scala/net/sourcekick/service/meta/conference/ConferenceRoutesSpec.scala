package net.sourcekick.service.meta.conference

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration.DurationInt

class ConferenceRoutesSpec extends WordSpec with Matchers with ScalatestRouteTest {

  implicit def default(implicit system: ActorSystem): RouteTestTimeout = RouteTestTimeout(new DurationInt(5).second)

  private def conferencesPath(conferenceUuid: String) = s"/api/v1/conferences/$conferenceUuid"

  private val globalRoutes: GlobalRoutes =
    new GlobalRoutes(scala.concurrent.ExecutionContext.Implicits.global, materializer)

  private val sealedEntryPoint = Route.seal(globalRoutes.entryPoint)

  // ----- Spec -----
  "The PointRoutes" must {

    val conferenceUuid = UUID.randomUUID().toString

    "create get update and get a Conference" in {
      ???
    }

  }

}
