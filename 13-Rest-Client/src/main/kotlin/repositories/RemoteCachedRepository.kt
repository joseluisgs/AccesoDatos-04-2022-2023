package repositories

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mappers.toUserModel
import models.User
import mu.KotlinLogging
import services.katorfit.KtorApi
import services.sqldelight.SqlDeLight

private val logger = KotlinLogging.logger {}
private const val REFRESH_TIME = 6 * 2000L // 6 seconds, el tiempo que tarda en refrescar

class RemoteCachedRepository {
    // Inyectar dependencias
    private val remote = KtorApi.client
    private val cached = SqlDeLight.client


    suspend fun refresh() = withContext(Dispatchers.IO) {
        // Lanzamos una corutina para que se ejecute en segundo plano
        logger.debug { "RemoteCachedRepository.refresh()" }
        launch {
            do {
                logger.debug { "RemoteCachedRepository.refresh()" }
                cached.removeAllUsers()
                remote.getAll(0, 500).data.forEach { user ->
                    cached.insertUser(user.id.toLong(), user.firstName, user.lastName, user.email, user.avatar)
                }
                delay(REFRESH_TIME)
            } while (true)

        }
    }

    fun findAll(): Flow<List<User>> {
        // De esta manera me quedo escuchando en tiempo real!!!
        // Si no devolver como una lista
        logger.debug { "RemoteCachedRepository.getAll()" }
        return cached.selectUsers().asFlow().mapToList(Dispatchers.IO)
            .map { it.map { user -> user.toUserModel() } }
    }

    fun findById(id: Long): User {
        logger.debug { "RemoteCachedRepository.findById(id=$id)" }
        // consulamos la base de datos local
        return cached.selectById(id).executeAsOne().toUserModel()
    }

    suspend fun save(entity: User): User {
        // Insertamos remotamente y localmente (mirar el orden por la id, si es uuid lo hacemos antes localmente)
        // si dependemos de una base de datos remota se lo debemos pedir a la base de datos remota
        logger.debug { "RemoteCachedRepository.save(entity=$entity)" }
        val dto = remote.create(entity)
        val user = User(
            id = dto.id,
            firstName = dto.firstName,
            lastName = dto.lastName,
            avatar = dto.avatar,
            email = dto.email
        )
        cached.insertUser(user.id, user.firstName, user.lastName, user.email, user.avatar)
        // Devolvemos pero con la id que nos ha devuelto el servidor
        //return cached.selectLastUser().executeAsOne().toUserModel()
        return user
    }

    suspend fun update(entity: User): User {
        // actualizamos localmente y remotamente
        logger.debug { "RemoteCachedRepository.update(entity=$entity)" }
        cached.update(
            id = entity.id,
            first_name = entity.firstName,
            last_name = entity.lastName,
            email = entity.email,
            avatar = entity.avatar
        )
        val dto = remote.update(entity.id, entity)
        return User(
            id = dto.id,
            firstName = dto.firstName,
            lastName = dto.lastName,
            avatar = dto.avatar,
            email = dto.email
        )
    }


    suspend fun delete(entity: User): User {
        // borramos localmente y remotamente
        logger.debug { "RemoteCachedRepository.delete(entity=$entity)" }
        cached.delete(entity.id)
        remote.delete(entity.id)
        return entity
    }
}

