package repositories

import com.github.michaelbull.result.Result
import errors.UserError
import kotlinx.coroutines.flow.Flow
import models.User

interface UsersRepository {
    suspend fun getAll(): Flow<List<User>>
    suspend fun getById(id: Long): Result<User, UserError>
    suspend fun getByEmail(email: String): Result<User, UserError>
    suspend fun getByUsername(username: String): Result<User, UserError>
    suspend fun save(user: User): Result<User, UserError>
    suspend fun delete(user: User): Result<User, UserError>
    suspend fun refresh(): Result<Unit, UserError>
}