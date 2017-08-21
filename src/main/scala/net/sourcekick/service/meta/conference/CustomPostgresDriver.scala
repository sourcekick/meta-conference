package net.sourcekick.service.meta.conference

import com.github.tminglei.slickpg._

trait CustomPostgresDriver extends ExPostgresProfile with PgCirceJsonSupport {

  // Define the kind of json store to use.
  def pgjson = "jsonb" // For postgresql 9.4 and higher use `jsonb`. Version below 9.4 must use `json`.

  // Add back `capabilities.insertOrUpdate` to enable native `upsert` support; for postgres 9.5+
  override protected def computeCapabilities: Set[slick.basic.Capability] =
    super.computeCapabilities + slick.jdbc.JdbcCapabilities.insertOrUpdate

  override val api: MyAPI.type = MyAPI

  object MyAPI extends API with CirceImplicits

}

/**
  * The object that will be used to provide postgresql functionalities for slick.
  */
object CustomPostgresDriver extends CustomPostgresDriver
