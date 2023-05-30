package repositories

import exceptions.RestException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import models.User
import mu.KotlinLogging
import services.katorfit.KtorApi

private val logger = KotlinLogging.logger {}

class KtorfitRepository : CrudRepository<User, Int> {

    override suspend fun findAll(page: Int, perPage: Int): Flow<User> = withContext(Dispatchers.IO) {
        logger.debug { "findAll(page=$page, perPage=$perPage)" }
        val call = KtorApi.client.getAll(page, perPage)
        try {
            logger.debug { "findAll(page=$page, perPage=$perPage) - OK" }
            return@withContext call.data.asFlow()
        } catch (e: Exception) {
            logger.error { "findAll(page=$page, perPage=$perPage) - ERROR" }
            throw RestException("Error al obtener los usuarios: ${e.message}")
        }

    }

    suspend fun findAllWithToken(token: String, page: Int, perPage: Int): Flow<List<User>> {
        logger.debug { "findAllWithToken(token=$token, page=$page, perPage=$perPage)" }
        return KtorApi.client.getAllWithToken(token, page, perPage).map { it.data }
    }

    override suspend fun findById(id: Int): User {
        logger.debug { "finById(id=$id)" }
        val call = KtorApi.client.getById(id)
        /*try {
            logger.debug { "findById(id=$id) - OK" }
            return call.data
        } catch (e: Exception) {
            logger.error { "findById(id=$id) - ERROR" }
            throw RestException("Error al obtener el usuario con id $id o no existe: ${e.message}")
        }*/

        // Mucho mejor con fold en Kotlin Result
        KtorApi.client.getById(id).fold(
            onSuccess = {
                logger.debug { "findById(id=$id) - OK" }
                return it.data
            },
            onFailure = {
                logger.error { "findById(id=$id) - ERROR" }
                throw RestException("Error al obtener el usuario con id $id o no existe: ${it.message}")
            }
        )
    }

    override suspend fun save(entity: User): User {
        logger.debug { "save(entity=$entity)" }
        try {
            val res = KtorApi.client.create(entity)
            logger.debug { "save(entity=$entity) - OK" }
            return User(
                id = res.id,
                firstName = res.firstName,
                lastName = res.lastName,
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
            val res = KtorApi.client.update(entity.id, entity)
            logger.debug { "update(entity=$entity) - OK" }
            return User(
                id = res.id,
                firstName = res.firstName,
                lastName = res.lastName,
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
            KtorApi.client.delete(entity.id)
            logger.debug { "delete(entity=$entity) - OK" }
            return entity
        } catch (e: Exception) {
            logger.error { "delete(entity=$entity) - ERROR" }
            throw RestException("Error al eliminar el usuario con ${entity.id}: ${e.message}")
        }
    }


}