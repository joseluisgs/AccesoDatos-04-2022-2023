package services.sqldelight

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.joseluisgs.database.AppDatabase
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

object SqlDeLight {

    val client = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).let {
        AppDatabase.Schema.create(it)
        AppDatabase(it).appDatabaseQueries
    }

    // limpiamos las tablas
    fun removeAllData() {
        logger.debug { "SqlDeLightClient.removeAllData()" }
        client.transaction {
            logger.debug { "SqlDeLightClient.removeAllData() - users " }
            client.removeAllUsers()
        }
    }


}