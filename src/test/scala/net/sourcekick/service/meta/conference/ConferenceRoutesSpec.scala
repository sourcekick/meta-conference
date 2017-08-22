package net.sourcekick.service.meta.conference

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

import akka.http.scaladsl.model.{ContentTypes, HttpCharsets, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import io.circe.java8.time._
import io.circe.generic.auto._
import slick.basic.DatabaseConfig

import scala.concurrent.duration.DurationInt

class ConferenceRoutesSpec extends WordSpec with Matchers with BeforeAndAfterAll with ScalatestRouteTest {

  //implicit def default(implicit system: ActorSystem): RouteTestTimeout = RouteTestTimeout(new DurationInt(5).second)
  implicit def default(): RouteTestTimeout = RouteTestTimeout(new DurationInt(5).second)

  private def conferencesPath(conferenceUuid: String) = s"/api/v1/conferences/$conferenceUuid"

  private def conferenceRepository = new ConferenceDatabaseModule(DatabaseConfig.forConfig("meta-conference.database"))

  private val globalRoutes: GlobalRoutes =
    new GlobalRoutes(scala.concurrent.ExecutionContext.Implicits.global, materializer, conferenceRepository)

  private val sealedEntryPoint = Route.seal(globalRoutes.entryPoint)

  override def beforeAll(): Unit = {
    val _ = 42
    //val _ = Await.result(conferenceRepository.createSchema(), 10.seconds)
  }

  // ----- Spec -----
  "The PointRoutes" must {

    val conferenceUuid = UUID.randomUUID().toString
    val conferenceFullPath = conferencesPath(conferenceUuid)
    val conference = Conference(conferenceUuid, "FrOSCon", Instant.now.minus(5L, ChronoUnit.DAYS), Instant.now)

    "create get update get remove and get a Conference" in {

      // Create conference
      Post(conferenceFullPath, conference) ~> sealedEntryPoint ~> check {
        status shouldEqual StatusCodes.Created
        contentType shouldEqual ContentTypes.`application/json`
        charset shouldEqual HttpCharsets.`UTF-8`

        responseAs[Conference] shouldEqual conference
      }

      // Test if conference exists
      Get(conferenceFullPath) ~> sealedEntryPoint ~> check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        charset shouldEqual HttpCharsets.`UTF-8`

        responseAs[Conference] shouldEqual conference
      }

      // Create updated conference
      val conferenceUpdate = Conference(conferenceUuid, "FrOSCon2", conference.from, conference.to)
      // Push updated conference to server
      Put(conferenceFullPath, conferenceUpdate) ~> sealedEntryPoint ~> check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        charset shouldEqual HttpCharsets.`UTF-8`

        responseAs[Conference] shouldEqual conferenceUpdate
      }

      // Test if the conference is updated
      Get(conferenceFullPath) ~> sealedEntryPoint ~> check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        charset shouldEqual HttpCharsets.`UTF-8`

        responseAs[Conference] shouldEqual conferenceUpdate
      }

      // Delete the confernce
      Delete(conferenceFullPath) ~> sealedEntryPoint ~> check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        charset shouldEqual HttpCharsets.`UTF-8`

        responseAs[Conference] shouldEqual 1
      }

      // Get new conference
      Get(conferenceFullPath) ~> sealedEntryPoint ~> check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        charset shouldEqual HttpCharsets.`UTF-8`

        responseAs[Conference] shouldEqual null
      }
    }
  }
}
