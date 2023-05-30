package repositories

import exceptions.RestException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import models.User
import mu.KotlinLogging
import services.retrofit.RetroApi

private val logger = KotlinLogging.logger {}

class RetrofitRepository : CrudRepository<User, Long> {
    // Inyectar dependencia
    override suspend fun findAll(page: Int, perPage: Int): Flow<User> = withContext(Dispatchers.IO) {
        logger.debug { "findAll(page=$page, perPage=$perPage)" }
        val call = RetroApi.client.getAll(page, perPage)
        val res = call.body()
        if (call.isSuccessful && res?.data != null) {
            logger.debug { "findAll(page=$page, perPage=$perPage) - ${call.code()} - ${call.isSuccessful}" }
            return@withContext res.data.asFlow()
        } else {
            logger.error { "findAll(page=$page, perPage=$perPage) -  ${call.errorBody()}" }
            throw RestException("Error al obtener los usuarios: ${call.errorBody()}")
        }

    }

    suspend fun findAllWithToken(token: String, page: Int, perPage: Int): Flow<User> {
        logger.debug { "findAllWithToken(token=$token, page=$page, perPage=$perPage)" }
        val call = RetroApi.client.getAllWithToken(token, page, perPage)
        val res = call.body()
        if (call.isSuccessful && res?.data != null) {
            logger.debug { "findAllWithToken(token=$token, page=$page, perPage=$perPage) - ${call.code()} - ${call.isSuccessful}" }
            return res.data.asFlow()
        } else {
            logger.error { "findAllWithToken(token=$token, page=$page, perPage=$perPage) - ${call.errorBody()}} - ${call.isSuccessful}" }
            throw RestException("Error al obtener los usuarios: ${call.errorBody()}")
        }
    }

    override suspend fun findById(id: Long): User {
        logger.debug { "finById(id=$id)" }
        val call = RetroApi.client.getById(id)
        val res = call.body()
        if (call.isSuccessful && res?.data != null) {
            logger.debug { "findById(id=$id) - ${call.code()} - ${call.isSuccessful}" }
            return res.data
        } else {
            logger.error { "findById(id=$id) - ${call.code()} - ${call.errorBody()}" }
            throw RestException("Error al obtener el usuario: ${call.errorBody()}")
        }
    }

    override suspend fun save(entity: User): User {
        logger.debug { "save(entity=$entity)" }
        val call = RetroApi.client.create(entity)
        val res = call.body()
        if (call.isSuccessful && res != null) {
            logger.debug { "save(entity=$entity) - ${call.code()} - ${call.isSuccessful}" }
            return User(
                id = res.id,
                firstName = res.firstName,
                lastName = res.lastName,
                avatar = res.avatar,
                email = res.email,
            )
        } else {
            logger.error { "save(entity=$entity) - ${call.code()} - ${call.errorBody()}" }
            throw RestException("Error al crear el usuario: ${call.errorBody()}")
        }

    }

    override suspend fun update(entity: User): User {
        logger.debug { "update(entity=$entity)" }
        val call = RetroApi.client.update(entity.id, entity)
        val res = call.body()
        if (call.isSuccessful && res != null) {
            logger.debug { "update(entity=$entity) - ${call.code()} - ${call.isSuccessful}" }
            return User(
                id = res.id,
                firstName = res.firstName,
                lastName = res.lastName,
                avatar = res.avatar,
                email = res.email,
            )
        } else {
            logger.error { "update(entity=$entity) - ${call.code()} - ${call.errorBody()}" }
            throw RestException("Error al actualizar el usuario: ${call.errorBody()}")
        }
    }

    override suspend fun delete(entity: User): User {
        logger.debug { "delete(entity=$entity)" }
        val call = RetroApi.client.delete(entity.id)
        if (call.isSuccessful) {
            logger.debug { "delete(entity=$entity) - ${call.code()} - ${call.isSuccessful}" }
            return entity
        } else {
            logger.error { "delete(entity=$entity) - ${call.code()} - ${call.errorBody()}" }
            throw RestException("Error al eliminar el usuario: ${call.errorBody()}")
        }
    }
}