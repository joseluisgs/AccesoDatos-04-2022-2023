package repositories

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloException
import graphql.rocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import services.RocketClient

private val logger = KotlinLogging.logger {}

class RocketRepository {
    // inyectar dependencias
    private val client: ApolloClient = RocketClient.getInstance()

    suspend fun getLaunchList(): List<LaunchListQuery.Launch> = withContext(Dispatchers.IO) {
        logger.debug { "getLaunchList" }
        try {
            val response = client.query(LaunchListQuery()).execute()
            // println(response.data)
            if (response.data != null && !response.hasErrors()) {
                logger.debug { "getLaunchList: ${response.data}" }
                return@withContext response.data!!.launches.launches as List<LaunchListQuery.Launch>
            } else {
                logger.error { "getLaunchList: ${response.errors}" }
            }
        } catch (e: Exception) {
            logger.error { "getLaunchList: ${e.message}" }
        }
        return@withContext emptyList<LaunchListQuery.Launch>()
    }

    // Y nos quedamos conectados!!!
    fun getLaunchListFlow(): Flow<List<LaunchListQuery.Launch?>> {
        logger.debug { "getLaunchListFlow" }
        println("Query -> LaunchList as Flow")
        return client.query(LaunchListQuery())
            .toFlow().map { it.data?.launches!!.launches }.flowOn(Dispatchers.IO)
    }

    suspend fun getLaunchById(id: String = "1"): LaunchDetailsQuery.Launch? = withContext(Dispatchers.IO) {
        logger.debug { "getLaunchById" }
        val response = client.query(LaunchDetailsQuery(id)).execute()
        try {
            if (response.data != null && !response.hasErrors()) {
                return@withContext response.data?.launch
            } else {
                logger.error { "getLaunchById: ${response.errors}" }
            }
        } catch (e: ApolloException) {
            logger.error { "getLaunchById: ${e.message}" }
        }
        return@withContext null
    }

    suspend fun cancelLaunch(id: String, token: String): CancelTripMutation.CancelTrip? = withContext(Dispatchers.IO) {
        logger.debug { "cancelLaunch" }
        // Necesitamos una instancia con el token
        val clientToken = RocketClient.getInstance(token)
        val response = clientToken.mutation(CancelTripMutation(id)).execute()
        try {
            if (response.data != null && !response.hasErrors()) {
                return@withContext response.data?.cancelTrip
            } else {
                logger.error { "cancelLaunch: ${response.errors}" }
            }
        } catch (e: ApolloException) {
            logger.error { "cancelLaunch: ${e.message}" }
        }
        return@withContext null
    }

    suspend fun bookLaunch(id: String, token: String): BookTripMutation.BookTrips? = withContext(Dispatchers.IO) {
        logger.debug { "bookLaunch" }
        // Necesitamos una instancia con el token
        val clientToken = RocketClient.getInstance(token)
        val response = clientToken.mutation(BookTripMutation(id)).execute()
        try {
            if (response.data != null && !response.hasErrors()) {
                return@withContext response.data?.bookTrips
            } else {
                logger.error { "bookLaunch: ${response.errors}" }
            }
        } catch (e: ApolloException) {
            logger.error { "bookLaunch: ${e.message}" }
        }
        return@withContext null
    }

    fun tripsBookedSubscription(token: String): Flow<ApolloResponse<TripsBookedSubscription.Data>> {
        logger.debug { "tripsBookedSubscription" }
        val clientToken = RocketClient.getInstance(token)
        return clientToken.subscription(TripsBookedSubscription()).toFlow().flowOn(Dispatchers.IO)
    }

}