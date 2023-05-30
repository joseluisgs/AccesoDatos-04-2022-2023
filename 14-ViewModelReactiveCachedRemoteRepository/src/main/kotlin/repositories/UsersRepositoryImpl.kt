package repositories

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.github.michaelbull.result.*
import errors.UserError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import mappers.toDto
import mappers.toEntity
import mappers.toModel
import models.User
import mu.KotlinLogging
import org.koin.core.annotation.Singleton
import services.database.SqlDeLightClient
import services.remote.KtorFitClient

private val logger = KotlinLogging.logger {}

@Singleton
class UsersRepositoryImpl(
    private val remote: KtorFitClient,
    private val local: SqlDeLightClient
) : UsersRepository {
    override suspend fun getAll(): Flow<List<User>> = withContext(Dispatchers.IO) {
        logger.debug { "Getting all users from local" }

        local.client.selectAll().asFlow().mapToList(Dispatchers.IO)
            .map {
                it.toModel()
            }
    }

    override suspend fun getById(id: Long): Result<User, UserError> = withContext(Dispatchers.IO) {
        logger.debug { "Getting user with id $id from local" }

        return@withContext local.client.selectById(id).executeAsOneOrNull()?.let {
            Ok(it.toModel())
        } ?: Err(UserError.NotFound("User with id $id not found"))

    }

    override suspend fun getByEmail(email: String): Result<User, UserError> {
        logger.debug { "Getting user with email $email from local" }

        return local.client.selectByEmail(email).executeAsOneOrNull()?.let {
            Ok(it.toModel())
        } ?: Err(UserError.NotFound("User with email $email not found"))
    }

    override suspend fun getByUsername(username: String): Result<User, UserError> {
        logger.debug { "Getting user with username $username from local" }

        return local.client.selectByUsername(username).executeAsOneOrNull()?.let {
            Ok(it.toModel())
        } ?: Err(UserError.NotFound("User with username $username not found"))
    }

    override suspend fun save(user: User): Result<User, UserError> = withContext(Dispatchers.IO) {
        logger.debug { "Saving user with id ${user.id}" }
        return@withContext if (user.id == User.NEW_USER_ID) {
            create(user)
        } else {
            update(user)
        }
    }

    private suspend fun create(user: User): Result<User, UserError> {
        logger.debug { "Creating user with id ${user}" }

        // Comenta alguno!!!

        // // Si usamos el Response de KtorFit
        return remote.checkService()
            .mapError { UserError.CreateUser(it.message) }
            .andThen {
                // Si usamos el Response de KtorFit
                val res = remote.client.save(user.toDto())
                if (res.isSuccessful) {
                    // Salvar en local
                    local.client.insertEntity(res.body()!!.toModel().toEntity())
                    Ok(res.body()!!.toModel())
                } else {
                    Err(UserError.CreateUser("Error creating user ${res.status} - ${res.errorBody()}"))
                }
            }

        // Si usamos el Result de Kotlin
        /* return remote.checkService()
             .mapError { UserError.CreateUser(it.message) }
             .andThen {
                 remote.client.create(user.toDto()).fold(
                     onSuccess = {
                         // Salvar en local, àra que no de error por id repetido lo cambio en este ejemplo
                         local.client.insertEntity(it.toModel().toEntity())
                         Ok(it.toModel())
                     },
                     onFailure = {
                         Err(UserError.CreateUser("Error creating user ${it.message}"))
                     }
                 )
             }*/
    }


    private suspend fun update(user: User): Result<User, UserError> {
        logger.debug { "Updating user with id ${user.id}" }

        // Si usamos el Response de
        return remote.checkService()
            .mapError { UserError.UpdateUser(it.message) }.andThen {
                val res = remote.client.replace(user.id, user.toDto())
                if (res.isSuccessful) {
                    // Salvar en local
                    local.client.update(
                        id = res.body()!!.id,
                        name = res.body()!!.name,
                        username = res.body()!!.username,
                        email = res.body()!!.email,
                        phone = res.body()!!.phone,
                        website = res.body()!!.website
                    )
                    Ok(res.body()!!.toModel())
                } else {
                    Err(UserError.UpdateUser("Error creating user ${res.status} - ${res.errorBody()}"))
                }
            }

        // Si usamos el Result de Kotlin
        /*return remote.checkService()
            .mapError { UserError.UpdateUser(it.message) }.andThen {
                remote.client.update(user.id, user.toDto()).fold(
                    onSuccess = {
                        // Salvar en local
                        local.client.update(
                            id = it.id,
                            name = it.name,
                            username = it.username,
                            email = it.email,
                            phone = it.phone,
                            website = it.website
                        )
                        Ok(it.toModel())
                    },
                    onFailure = {
                        Err(UserError.UpdateUser("Error creating user ${it.message}"))
                    }
                )
            }*/
    }


    override suspend fun delete(user: User): Result<User, UserError> = withContext(Dispatchers.IO) {
        logger.debug { "Deleting user with id ${user.id}" }

        return@withContext remote.checkService()
            .mapError { UserError.DeleteUser(it.message) }.andThen {
                val res = remote.client.remove(user.id)
                if (res.isSuccessful) {
                    // Salvar en local
                    local.client.delete(user.id)
                    Ok(user)
                } else {
                    Err(UserError.DeleteUser("Error deleting user ${res.status} - ${res.errorBody()}"))
                }
            }

        /*return@withContext remote.checkService()
            .mapError { UserError.DeleteUser(it.message) }.andThen {
                remote.client.delete(user.id).fold(
                    onSuccess = {
                        // Salvar en local
                        local.client.delete(user.id)
                        Ok(user)
                    },
                    onFailure = {
                        Err(UserError.UpdateUser("Error deleting user ${it.message}"))
                    }
                )
            }*/

    }

    override suspend fun refresh(): Result<Unit, UserError> = withContext(Dispatchers.IO) {
        logger.debug { "Refreshing users" }
        // Borrar todos los datos
        local.client.deleteAll()
        // Obtener datos de remoto
        return@withContext remote.checkService()
            .mapError { UserError.DeleteUser(it.message) }.andThen {
                remote.client.getAll()
                    .first().forEach {
                        local.client.insertEntity(it.toModel().toEntity())
                    }
                Ok(Unit)
            }
    }
}