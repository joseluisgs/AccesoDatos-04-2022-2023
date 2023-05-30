package services.retrofit

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dto.LoginDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import okhttp3.MediaType
import retrofit2.Retrofit

// https://square.github.io/retrofit/
private val logger = KotlinLogging.logger {}

object RetroApi {
    private const val API_URL = "https://reqres.in/"
    private val contentType = MediaType.get("application/json")

    @OptIn(ExperimentalSerializationApi::class)
    val client = Retrofit.Builder().baseUrl(API_URL)
        // Nuestro conversor de JSON
        .addConverterFactory(Json.asConverterFactory(contentType))
        .build()
        .create(RetroFitRest::class.java)

    suspend fun login(login: LoginDto): String = withContext(Dispatchers.IO) {
        logger.debug { "login(login=$login)" }
        val response = client.login(login)
        val token = response.body()?.token ?: throw Exception("Error al hacer login")
        logger.debug { "login(login=$login) - OK" }
        return@withContext token
    }
}
