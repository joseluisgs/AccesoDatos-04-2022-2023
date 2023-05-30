package services.katorfit

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.SuspendResponseConverter
import de.jensklingenberg.ktorfit.converter.builtin.FlowResponseConverter
import de.jensklingenberg.ktorfit.internal.TypeData
import dto.LoginDto
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import mu.KotlinLogging


// https://foso.github.io/Ktorfit/
// https://ktor.io/docs/getting-started-ktor-client.html

private val logger = KotlinLogging.logger {}

object KtorApi {
    private const val API_URL = "https://reqres.in/"

    // Esta vez con lazy
    val client by lazy {
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
            .responseConverter(FlowResponseConverter()) // Podemos meterle flow directamente!!!
            .responseConverter(KotlinResultConverter()) // Podemos meterle el Result de Kotlin
            .build().create<KtorFitRest>()
    }


    suspend fun login(login: LoginDto): String = withContext(Dispatchers.IO) {
        logger.debug { "login(login=$login)" }
        val response = client.login(login)
        val token = response.token
        logger.debug { "login(login=$login) - OK" }
        return@withContext token
    }
}

// Ahora podemos usar Kotlin.Result y Kotlin.Flow directamente en el cliente Mira la interfaz KtorFitRest
class KotlinResultConverter : SuspendResponseConverter {
    override fun supportedType(typeData: TypeData, isSuspend: Boolean): Boolean {
        return typeData.qualifiedName == "kotlin.Result"
    }

    override suspend fun <RequestType> wrapSuspendResponse(
        typeData: TypeData,
        requestFunction: suspend () -> Pair<TypeInfo, HttpResponse>,
        ktorfit: Ktorfit
    ): Any {
        return try {
            val (info, response) = requestFunction()
            Result.success<Any>(response.body(info))
        } catch (ex: Throwable) {
            Result.failure(ex)
        }
    }
}


