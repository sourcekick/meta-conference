/**
  * Copyright (C) Recogizer Group GmbH - All Rights Reserved
  * Unauthorized copying of this file, via any medium is strictly prohibited
  * Proprietary and confidential
  * Created on 21.08.17.
  */
package net.sourcekick.service.meta.conference

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers, ParallelTestExecution}
import slick.basic.DatabaseConfig

import scala.concurrent.Await
import scala.concurrent.duration._

private object ConferenceRepositorySpec {

  // when using ParallelTestExecution we need to hold the references to our shared fixture outside of the Spec class
  // because apparently for each test case in the Spec the Spec class is instantiated individually
  // therefore we simply keep these references here in the companion object

  private def conferenceRepository = new ConferenceDatabaseModule(DatabaseConfig.forConfig("meta-conference.database"))

}

class ConferenceRepositorySpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll with ParallelTestExecution {
  import ConferenceRepositorySpec._

  override def beforeAll(): Unit = {
    val _ = Await.result(conferenceRepository.createSchema(), 10.seconds)
  }

  "The DeviceRepository" must {

    "return nothing if there is nothing for a given primary key" in {
      conferenceRepository
        .loadConference(UUID.randomUUID().toString)
        .map(found => {
          found shouldBe 'empty
        })
    }

    "create and load conference" in {
      val conferenceUuid = UUID.randomUUID().toString
      val conference = Conference(conferenceUuid, "FrOSCon", Instant.now.minus(5L, ChronoUnit.DAYS), Instant.now)
      conferenceRepository
        .createConference(conference)
        .map(createdConference => {
          createdConference.get shouldBe conference
        })
        .flatMap(_ => {
          conferenceRepository.loadConference(conferenceUuid)
        })
        .map(found => {
          found shouldBe 'isDefined
          found.get shouldBe conference
        })
    }

    // TODO priority 2 add a test case with: create, load, update, load

  }

}
