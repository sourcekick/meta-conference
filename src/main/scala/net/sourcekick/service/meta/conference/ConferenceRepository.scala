package net.sourcekick.service.meta.conference

import scala.concurrent.{ExecutionContext, Future}

trait ConferenceRepository {

  def createSchema(): Future[Unit]

  def createConference(conference: Conference): Future[Option[Conference]]

  def loadConference(conferenceUuid: String): Future[Option[Conference]]

  def updateConference(conference: Conference)(implicit ec: ExecutionContext): Future[Option[Conference]]

  def removeConference(conferenceUuid: String)(implicit ec: ExecutionContext): Future[Int]

  def closeRepository(): Unit

}
