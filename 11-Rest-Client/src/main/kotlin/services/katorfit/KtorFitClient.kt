package services.katorfit

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.create
import dto.LoginDto
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import mu.KotlinLogging


// https://foso.github.io/Ktorfit/
// https://ktor.io/docs/getting-started-ktor-client.html

private val logger = KotlinLogging.logger {}

object KtorFitClient {
    private const val API_URL = "https://reqres.in/"

    private val ktorfit by lazy {
        // Podemos meterle flow directamente!!!
        // ktorfit.responseConverter(FlowResponseConverter())
        Ktorfit.Builder()
            .httpClient {
                install(ContentNegotiation) {
                    json(Json { isLenient = true; ignoreUnknownKeys = true })
                }
                install(DefaultRequest) {
                    header(HttpHeaders.ContentType, ContentType.Application.Json)
                }
            }
            .baseUrl(API_URL)
            .build()
    }

    // Creamos una instancia de Retrofit con las llamadas a la API
    val instance by lazy {
        ktorfit.create<KtorFitRest>()
    }

    suspend fun login(login: LoginDto): String = withContext(Dispatchers.IO) {
        logger.debug { "login(login=$login)" }
        val response = instance.login(login)
        val token = response.token ?: throw Exception("Error al hacer login")
        logger.debug { "login(login=$login) - OK" }
        return@withContext token
    }
}