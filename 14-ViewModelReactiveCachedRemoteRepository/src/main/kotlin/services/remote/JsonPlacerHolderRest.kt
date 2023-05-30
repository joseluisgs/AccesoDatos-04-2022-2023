package services.remote

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.*
import dto.placeholder.UserDto
import kotlinx.coroutines.flow.Flow

// Usamos la nueva clase Response de KtorFit para hacer el wrapper
// si no usar try catch con objeto completo
interface JsonPlacerHolderRest {

    @GET("users")
    fun getAll(): Flow<List<UserDto>>

    @GET("users/{id}")
    suspend fun getById(@Path("id") id: Int): Response<UserDto>

    @POST("users")
    suspend fun save(@Body user: UserDto): Response<UserDto>

    @POST("users")
    suspend fun create(@Body user: UserDto): Result<UserDto>

    @PUT("users/{id}")
    suspend fun replace(@Path("id") id: Long, @Body user: UserDto): Response<UserDto>

    @PUT("users/{id}")
    suspend fun update(@Path("id") id: Long, @Body user: UserDto): Result<UserDto>

    @DELETE("users/{id}")
    suspend fun delete(@Path("id") id: Long): Result<Unit>

    @DELETE("users/{id}")
    suspend fun remove(@Path("id") id: Long): Response<Unit>

}