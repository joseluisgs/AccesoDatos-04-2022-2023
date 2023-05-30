package services.katorfit

import de.jensklingenberg.ktorfit.http.*
import dto.*
import kotlinx.coroutines.flow.Flow
import models.User

interface KtorFitRest {

    @GET("api/users")
    suspend fun getAll(@Query("page") page: Int = 0, @Query("per_page") perPage: Int = 0): GetAllDto

    // Podemos usar Result de Kotlin para evitar estar Try/Catch
    @GET("api/users/{id}")
    suspend fun getById(@Path("id") id: Int): Result<GetByIdDto>

    @POST("api/users")
    suspend fun create(@Body user: User): CreateDto

    @PUT("api/users/{id}")
    suspend fun update(@Path("id") id: Long, @Body user: User): UpdateDto

    @PATCH("api/users/{id}")
    suspend fun upgrade(@Path("id") id: Long, @Body user: User): UpdateDto

    @DELETE("api/users/{id}")
    suspend fun delete(@Path("id") id: Long): Unit // Es void porque no devuelve nada

    @POST("api/login")
    suspend fun login(@Body user: LoginDto): TokenDto

    // Tambien podemos usar Flow con listas, aunque ahora lo haga con un objeto
    @GET("api/users")
    fun getAllWithToken(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 0,
        @Query("per_page") perPage: Int = 0
    ): Flow<GetAllDto>
}