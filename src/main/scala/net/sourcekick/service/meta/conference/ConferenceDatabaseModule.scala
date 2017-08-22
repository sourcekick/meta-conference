package net.sourcekick.service.meta.conference

import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class ConferenceDatabaseModule(dbConfig: DatabaseConfig[JdbcProfile]) extends ConferenceRepository {

  import dbConfig.profile.api._

  private val db = dbConfig.db
  private val conferences = TableQuery[ConferenceTable]

  private def queryFindConference(uuid: String) =
    conferences.filter(_.uuid === uuid)

  private def actionFindConference(uuid: String) =
    queryFindConference(uuid).result.headOption

  override def createSchema(): Future[Unit] =
    db.run(DBIO.seq(conferences.schema.create))

  override def createConference(conference: Conference): Future[Option[Conference]] = {
    val action = (conferences += conference) // go directly for an insert which will fail if the UUID already exists
      .andThen(actionFindConference(conference.uuid)) // retrieve inserted device
      .transactionally

    db.run(action)
  }

  override def loadConference(conferenceUuid: String): Future[Option[Conference]] =
    db.run(
      conferences
        .filter(r => r.uuid === conferenceUuid)
        .result
        .headOption)

  override def updateConference(conference: Conference)(implicit ec: ExecutionContext): Future[Option[Conference]] =
    db.run(
      conferences
        .filter(_.uuid === conference.uuid)
        .update(conference)
        .map {
          case 0 => None
          case _ => Option(conference)
        }
    )

  override def removeConference(conferenceUuid: String)(implicit ec: ExecutionContext): Future[Int] =
    db.run(
      conferences
        .filter(r => r.uuid === conferenceUuid)
        .delete
    )

  override def closeRepository(): Unit = db.close()

}
