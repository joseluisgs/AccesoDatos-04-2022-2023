package repositories.raquetas

import db.MongoDbManager
import models.Raqueta
import mu.KotlinLogging
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection
import org.litote.kmongo.save
import repositories.tenistas.RaquetasRepository

private val logger = KotlinLogging.logger {}

class RaquetasRepositoryImpl : RaquetasRepository {
    override fun findAll(): List<Raqueta> {
        logger.debug { "findAll" }
        return MongoDbManager.database.getCollection<Raqueta>().find().toList()
    }

    override fun findById(id: String): Raqueta? {
        logger.debug { "findById($id)" }
        return MongoDbManager.database.getCollection<Raqueta>().findOneById(id)
    }

    override fun save(entity: Raqueta): Raqueta {
        logger.debug { "save($entity)" }
        MongoDbManager.database.getCollection<Raqueta>().save(entity)
        return entity
    }

    private fun insert(entity: Raqueta): Raqueta {
        logger.debug { "save($entity) - creando" }
        MongoDbManager.database.getCollection<Raqueta>().save(entity)
        return entity
    }

    private fun update(entity: Raqueta): Raqueta {
        logger.debug { "save($entity) - actualizando" }
        MongoDbManager.database.getCollection<Raqueta>().save(entity)
        return entity
    }

    override fun delete(entity: Raqueta): Boolean {
        logger.debug { "delete($entity)" }
        return MongoDbManager.database.getCollection<Raqueta>().deleteOneById(entity.id).wasAcknowledged()
    }
}