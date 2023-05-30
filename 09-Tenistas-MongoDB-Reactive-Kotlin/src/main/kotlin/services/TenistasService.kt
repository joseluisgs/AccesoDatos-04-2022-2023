package services

import com.mongodb.reactivestreams.client.ChangeStreamPublisher
import db.MongoDbManager
import models.Tenista
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class TenistasService {
    fun watch(): ChangeStreamPublisher<Tenista> {
        // esta parte solo funciona con Mongo Atlas o hacinéndote una replica set local
        // https://www.mongodb.com/docs/drivers/java/sync/current/usage-examples/watch/
        // https://www.mongodb.com/docs/manual/reference/method/Mongo.watch/
        logger.debug { "watch()" }
        return MongoDbManager.database.getCollection<Tenista>()
            .watch<Tenista>()
            .publisher // así me ahorro ponerlo en donde vaya a consumir
    }
}