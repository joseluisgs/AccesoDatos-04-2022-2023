package services.database

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.joseluisgs.database.AppDatabase
import mu.KotlinLogging
import org.koin.core.annotation.Property
import org.koin.core.annotation.Singleton

private val logger = KotlinLogging.logger {}

@Singleton
class SqlDeLightClient(
    @Property("database.url")
    val databaseUrl: String,
    @Property("database.init")
    val databaseInit: String
) {

    val client = JdbcSqliteDriver(databaseUrl).let {
        logger.debug { "SqlDeLightClient.client() - create schema" }
        AppDatabase.Schema.create(it)
        if (databaseInit.toBooleanStrict()) {
            logger.debug { "SqlDeLightClient.client() - init database" }
            AppDatabase(it).appDatabaseQueries.transaction {
                AppDatabase(it).appDatabaseQueries.deleteAll()
            }
        }
        logger.debug { "SqlDeLightClient.client() - create client" }
        AppDatabase(it).appDatabaseQueries
    }
}