package com.aziza.shared.cache.shared

import com.aziza.shared.cache.AppDatabase
import com.aziza.shared.cache.AppDatabaseQueries
import com.aziza.shared.cache.Launch
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.TransacterImpl
import com.squareup.sqldelight.`internal`.copyOnWriteList
import com.squareup.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Unit
import kotlin.collections.MutableList
import kotlin.reflect.KClass

internal val KClass<AppDatabase>.schema: SqlDriver.Schema
  get() = AppDatabaseImpl.Schema

internal fun KClass<AppDatabase>.newInstance(driver: SqlDriver): AppDatabase =
    AppDatabaseImpl(driver)

private class AppDatabaseImpl(
  driver: SqlDriver
) : TransacterImpl(driver), AppDatabase {
  public override val appDatabaseQueries: AppDatabaseQueriesImpl = AppDatabaseQueriesImpl(this,
      driver)

  public object Schema : SqlDriver.Schema {
    public override val version: Int
      get() = 1

    public override fun create(driver: SqlDriver): Unit {
      driver.execute(null, """
          |CREATE TABLE Launch (
          |    flightNumber INTEGER NOT NULL,
          |    missionName TEXT NOT NULL,
          |    details TEXT,
          |    launchSuccess INTEGER DEFAULT NULL,
          |    launchDateUTC TEXT NOT NULL,
          |    patchUrlSmall TEXT,
          |    patchUrlLarge TEXT,
          |    articleUrl TEXT
          |)
          """.trimMargin(), 0)
    }

    public override fun migrate(
      driver: SqlDriver,
      oldVersion: Int,
      newVersion: Int
    ): Unit {
    }
  }
}

private class AppDatabaseQueriesImpl(
  private val database: AppDatabaseImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), AppDatabaseQueries {
  internal val selectAllLaunchesInfo: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> selectAllLaunchesInfo(mapper: (
    flightNumber: Long,
    missionName: String,
    details: String?,
    launchSuccess: Boolean?,
    launchDateUTC: String,
    patchUrlSmall: String?,
    patchUrlLarge: String?,
    articleUrl: String?
  ) -> T): Query<T> = Query(1643509639, selectAllLaunchesInfo, driver, "AppDatabase.sq",
      "selectAllLaunchesInfo", """
  |SELECT Launch.*
  |FROM Launch
  """.trimMargin()) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2),
      cursor.getLong(3)?.let { it == 1L },
      cursor.getString(4)!!,
      cursor.getString(5),
      cursor.getString(6),
      cursor.getString(7)
    )
  }

  public override fun selectAllLaunchesInfo(): Query<Launch> = selectAllLaunchesInfo { flightNumber,
      missionName, details, launchSuccess, launchDateUTC, patchUrlSmall, patchUrlLarge,
      articleUrl ->
    Launch(
      flightNumber,
      missionName,
      details,
      launchSuccess,
      launchDateUTC,
      patchUrlSmall,
      patchUrlLarge,
      articleUrl
    )
  }

  public override fun insertLaunch(
    flightNumber: Long,
    missionName: String,
    details: String?,
    launchSuccess: Boolean?,
    launchDateUTC: String,
    patchUrlSmall: String?,
    patchUrlLarge: String?,
    articleUrl: String?
  ): Unit {
    driver.execute(-1718362503, """
    |INSERT INTO Launch(flightNumber, missionName, details, launchSuccess, launchDateUTC, patchUrlSmall, patchUrlLarge, articleUrl)
    |VALUES(?, ?, ?, ?, ?, ?, ?, ?)
    """.trimMargin(), 8) {
      bindLong(1, flightNumber)
      bindString(2, missionName)
      bindString(3, details)
      bindLong(4, launchSuccess?.let { if (it) 1L else 0L })
      bindString(5, launchDateUTC)
      bindString(6, patchUrlSmall)
      bindString(7, patchUrlLarge)
      bindString(8, articleUrl)
    }
    notifyQueries(-1718362503, {database.appDatabaseQueries.selectAllLaunchesInfo})
  }

  public override fun removeAllLaunches(): Unit {
    driver.execute(-1136289103, """DELETE FROM Launch""", 0)
    notifyQueries(-1136289103, {database.appDatabaseQueries.selectAllLaunchesInfo})
  }
}
