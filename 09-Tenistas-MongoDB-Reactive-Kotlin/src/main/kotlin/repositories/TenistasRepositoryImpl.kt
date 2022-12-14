package repositories

import db.MongoDbManager
import exceptions.TenistaException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import models.Tenista
import mu.KotlinLogging
import org.litote.kmongo.Id

private val logger = KotlinLogging.logger {}

class TenistasRepositoryImpl : TenistasRepository {
    override fun findAll(): Flow<Tenista> {
        return MongoDbManager.database.getCollection<Tenista>()
            .find().publisher.asFlow()
    }

    override suspend fun findById(id: Id<Tenista>): Tenista {
        logger.debug { "findById($id)" }
        return MongoDbManager.database.getCollection<Tenista>()
            .findOneById(id) ?: throw TenistaException("No existe el tenista con id $id")
    }

    override suspend fun save(entity: Tenista): Tenista {
        logger.debug { "save($entity)" }
        return MongoDbManager.database.getCollection<Tenista>()
            .save(entity).let { entity }
    }

    private suspend fun insert(entity: Tenista): Tenista {
        logger.debug { "save($entity) - creando" }
        return MongoDbManager.database.getCollection<Tenista>()
            .save(entity).let { entity }
    }

    private suspend fun update(entity: Tenista): Tenista {
        logger.debug { "save($entity) - actualizando" }
        return MongoDbManager.database.getCollection<Tenista>()
            .save(entity).let { entity }
    }

    override suspend fun delete(entity: Tenista): Boolean {
        logger.debug { "delete($entity)" }
        return MongoDbManager.database.getCollection<Tenista>()
            .deleteOneById(entity.id).let { true }

        // o usar awaitFirstOrNull() y comparar con null
    }
}