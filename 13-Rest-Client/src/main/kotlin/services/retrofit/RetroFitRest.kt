package services.retrofit

import dto.*
import models.User
import retrofit2.Response
import retrofit2.http.*

interface RetroFitRest {

    @GET("api/users")
    suspend fun getAll(@Query("page") page: Int = 0, @Query("per_page") perPage: Int = 0): Response<GetAllDto>

    @GET("api/users/{id}")
    suspend fun getById(@Path("id") id: Long): Response<GetByIdDto>

    @POST("api/users")
    suspend fun create(@Body user: User): Response<CreateDto>

    @PUT("api/users/{id}")
    suspend fun update(@Path("id") id: Long, @Body user: User): Response<UpdateDto>

    @PATCH("api/users/{id}")
    suspend fun upgrade(@Path("id") id: Long, @Body user: User): Response<UpdateDto>

    @DELETE("api/users/{id}")
    suspend fun delete(@Path("id") id: Long): Response<Void> // Es void porque no devuelve nada

    @POST("api/login")
    suspend fun login(@Body user: LoginDto): Response<TokenDto>

    @GET("api/users")
    suspend fun getAllWithToken(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 0,
        @Query("per_page") perPage: Int = 0
    ): Response<GetAllDto>
}