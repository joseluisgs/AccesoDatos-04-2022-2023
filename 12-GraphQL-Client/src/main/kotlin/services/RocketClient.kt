package services

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.network.okHttpClient
import graphql.rocket.LoginMutation
import mu.KotlinLogging
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

private val logger = KotlinLogging.logger {}

object RocketClient {
    private val API_URL = "https://apollo-fullstack-tutorial.herokuapp.com/graphql"

    // Creamos una instancia de Apollo
    fun getInstance(token: String? = null): ApolloClient {

        // Añadimos un cliente okHttp para poder procesar las peticiones con token de autenticación
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor(token))
            .build()

        // Devolvemos el cliente Apollo
        return ApolloClient.Builder()
            .serverUrl(API_URL)
            // Le ponemos que esté todo chacheado
            .normalizedCache(MemoryCacheFactory(maxSizeBytes = 10 * 1024 * 1024))
            // Le metemos un websocket para las suscripciones
            .webSocketServerUrl("wss://apollo-fullstack-tutorial.herokuapp.com/graphql") // Opcional, solo suscripciones
            .okHttpClient(okHttpClient) // Opcional, solo por si queremos token o enviar en el header
            .build()
    }

    suspend fun login(email: String): String? {
        logger.debug { "login" }
        val response = getInstance()
            .mutation(LoginMutation(Optional.Present(email))).execute()
        try {
            if (response.data != null && !response.hasErrors()) {
                return response.data?.login?.token.toString()
            } else {
                logger.error { "login: ${response.errors}" }
            }
        } catch (e: ApolloException) {
            logger.error { "login: ${e.message}" }
        }
        return null
    }

    /**
     * Para añadir Header para tokens, mos creamos un interceptador
     */
    private class AuthorizationInterceptor(val token: String?) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                //addHeader("Authorization", "Bearer $token") --> Si es del tipo Bearer Token
                .addHeader("Authorization", token ?: "")
                .build()

            return chain.proceed(request)
        }
    }
}
