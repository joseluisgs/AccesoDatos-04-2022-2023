package repositories

import exceptions.RestException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import models.User
import mu.KotlinLogging
import services.katorfit.KtorFitClient

private val logger = KotlinLogging.logger {}

class KtorfitRepository : CrudRepository<User, Int> {
    // Inyectar dependencia
    private val client by lazy { KtorFitClient.instance }

    override suspend fun findAll(page: Int, perPage: Int): Flow<User> = withContext(Dispatchers.IO) {
        logger.debug { "findAll(page=$page, perPage=$perPage)" }
        val call = client.getAll(page, perPage)
        try {
            logger.debug { "findAll(page=$page, perPage=$perPage) - OK" }
            return@withContext call.data!!.asFlow()
        } catch (e: Exception) {
            logger.error { "findAll(page=$page, perPage=$perPage) - ERROR" }
            throw RestException("Error al obtener los usuarios: ${e.message}")
        }

    }

    suspend fun findAllWithToken(token: String, page: Int, perPage: Int): Flow<User> {
        logger.debug { "findAllWithToken(token=$token, page=$page, perPage=$perPage)" }
        val call = client.getAllWithToken(token, page, perPage)
        try {
            logger.debug { "findAllWithToken(token=$token, page=$page, perPage=$perPage) - OK" }
            return call.data!!.asFlow()
        } catch (e: Exception) {
            logger.error { "findAllWithToken(token=$token, page=$page, perPage=$perPage) - ERROR" }
            throw RestException("Error al obtener los usuarios: ${e.message}")
        }
    }

    override suspend fun findById(id: Int): User {
        logger.debug { "finById(id=$id)" }
        val call = client.getById(id)
        try {
            logger.debug { "findById(id=$id) - OK" }
            return call.data!!
        } catch (e: Exception) {
            logger.error { "findById(id=$id) - ERROR" }
            throw RestException("Error al obtener el usuario con id $id o no existe: ${e.message}")
        }
    }

    override suspend fun save(entity: User): User {
        logger.debug { "save(entity=$entity)" }
        try {
            val res = client.create(entity)
            logger.debug { "save(entity=$entity) - OK" }
            return User(
                id = res.id,
                first_name = res.first_name!!,
                last_name = res.last_name!!,
                avatar = res.avatar,
                email = res.email,
            )
        } catch (e: Exception) {
            logger.error { "save(entity=$entity) - ERROR" }
            throw RestException("Error al crear el usuario: ${e.message}")
        }

    }

    override suspend fun update(entity: User): User {
        logger.debug { "update(entity=$entity)" }
        try {
            val res = client.update(entity.id, entity)
            logger.debug { "update(entity=$entity) - OK" }
            return User(
                id = res.id,
                first_name = res.first_name,
                last_name = res.last_name,
                avatar = res.avatar,
                email = res.email,
            )
        } catch (e: RestException) {
            logger.error { "update(entity=$entity) - ERROR" }
            throw RestException("Error al actualizar el usuario con ${entity.id}: ${e.message}")
        }
    }

    override suspend fun delete(entity: User): User {
        logger.debug { "delete(entity=$entity)" }
        try {
            client.delete(entity.id)
            logger.debug { "delete(entity=$entity) - OK" }
            return entity
        } catch (e: Exception) {
            logger.error { "delete(entity=$entity) - ERROR" }
            throw RestException("Error al eliminar el usuario con ${entity.id}: ${e.message}")
        }
    }


}