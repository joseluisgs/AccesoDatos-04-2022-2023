package services.remote

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.builtin.FlowConverterFactory
import de.jensklingenberg.ktorfit.internal.TypeData
import errors.InternetError
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Singleton
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection

@Singleton
class KtorFitClient {
    private val API_URL = "http://jsonplaceholder.typicode.com/"

    // Esta vez con lazy
    val client = Ktorfit.Builder()
        .httpClient {
            install(ContentNegotiation) {
                json(Json { isLenient = true; ignoreUnknownKeys = true })
            }
            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }
        .baseUrl(API_URL)
        .converterFactories(FlowConverterFactory()) // Podemos meterle flow directamente!!!
        .converterFactories(KotlinResultConverterFactory()) // Y también Result!!!
        .build()
        .create<JsonPlacerHolderRest>()

    // Para algunas funciones
    fun checkService(): Result<Boolean, InternetError> {
        return try {
            val connection: URLConnection = URL(API_URL).openConnection()
            connection.connect()
            Ok(true)
        } catch (e: MalformedURLException) {
            Err(InternetError.MalformedUrl("Malformed URL: ${e.message}"))
        } catch (e: IOException) {
            Err(InternetError.NoInternet("No internet connection or unreachable endpoint: ${e.message}"))
        }
    }
}

// si queremos usar Result de Kotlin , no es obligatorio pues tenemos Response
class KotlinResultConverterFactory : Converter.Factory {
    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        if (typeData.typeInfo.type == kotlin.Result::class) {

            return object : Converter.SuspendResponseConverter<HttpResponse, Any> {
                override suspend fun convert(response: HttpResponse): Any {
                    return try {
                        kotlin.Result.success(response.body(typeData.typeArgs.first().typeInfo) as Any)
                    } catch (ex: Throwable) {
                        kotlin.Result.failure(ex)
                    }
                }
            }
        }
        return null
    }
}

