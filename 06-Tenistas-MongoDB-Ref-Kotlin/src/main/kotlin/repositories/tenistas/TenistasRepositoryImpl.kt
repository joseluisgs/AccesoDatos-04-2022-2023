package repositories.tenistas

import db.MongoDbManager
import db.lookup
import dto.TenistaDto
import dto.toTenistaDto
import models.Tenista
import mu.KotlinLogging
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection
import org.litote.kmongo.save

private val logger = KotlinLogging.logger {}

class TenistasRepositoryImpl : TenistasRepository {
    override fun findAll(): List<Tenista> {
        logger.debug { "findAll" }
        return MongoDbManager.database.getCollection<Tenista>().find().toList()
    }

    override fun findById(id: String): Tenista? {
        logger.debug { "findById($id)" }
        return MongoDbManager.database.getCollection<Tenista>().findOneById(id)
    }

    override fun save(entity: Tenista): Tenista {
        logger.debug { "save($entity)" }
        MongoDbManager.database.getCollection<Tenista>().save(entity)
        return entity
    }

    private fun insert(entity: Tenista): Tenista {
        logger.debug { "save($entity) - creando" }
        MongoDbManager.database.getCollection<Tenista>().save(entity)
        return entity
    }

    private fun update(entity: Tenista): Tenista {
        logger.debug { "save($entity) - actualizando" }
        MongoDbManager.database.getCollection<Tenista>().save(entity)
        return entity
    }

    override fun delete(entity: Tenista): Boolean {
        logger.debug { "delete($entity)" }
        return MongoDbManager.database.getCollection<Tenista>().deleteOneById(entity.id).wasAcknowledged()
    }

    fun findAllWithRaqueta(): List<TenistaDto> {
        logger.debug { "findAllWithRaquets" }
        return MongoDbManager.database.getCollection<Tenista>().lookup(
            otherCollection = "raqueta",
            localField = "raqueta_id",
            otherField = "_id",
            asField = "mi_raqueta"
        ) { it.toTenistaDto() }

        /*MongoDbManager.database.getCollection<Tenista>()
        val pipeline: Bson = lookup("raqueta", "raqueta_id", "_id", "mi_raqueta")
        return MongoDbManager.database.getCollection<Tenista>()
            .aggregate<Document>(pipeline)
            .map { it.toTenistaDto() }.toList()
        // Parece más lioso porque hago el mapper aquí, pero si no es un mapper de Document a la Entidad*/
    }
}