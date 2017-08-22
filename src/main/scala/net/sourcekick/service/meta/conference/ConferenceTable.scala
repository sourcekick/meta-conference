package net.sourcekick.service.meta.conference

import java.sql.Timestamp
import java.time.Instant

import slick.ast.BaseTypedType
import slick.jdbc.JdbcType
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{PrimaryKey, ProvenShape}

class ConferenceTable(tag: Tag) extends Table[Conference](tag, "conferences") {

  private implicit val instantColumnType: JdbcType[Instant] with BaseTypedType[Instant] =
    MappedColumnType.base[Instant, Timestamp](
      { instant =>
        new Timestamp(instant.toEpochMilli)
      }, { timestamp =>
        Instant.ofEpochMilli(timestamp.getTime)
      }
    )

  def uuid: Rep[String] = column[String]("uuid")

  def name: Rep[String] = column[String]("name")

  def from: Rep[Instant] = column[Instant]("from")

  def to: Rep[Instant] = column[Instant]("to")

  def conferencePk: PrimaryKey = primaryKey("conference_pk", uuid)

  def * : ProvenShape[Conference] = (uuid, name, from, to) <> ((Conference.apply _).tupled, Conference.unapply)

}
