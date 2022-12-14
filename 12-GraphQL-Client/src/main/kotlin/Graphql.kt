import kotlinx.coroutines.*
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import repositories.RocketRepository
import services.RocketClient
import kotlin.system.exitProcess

fun main(args: Array<String>): Unit = runBlocking {
    println("--------------------------------------------------------------------------------")
    println("API GraphQL Rocket.in - https://apollo-fullstack-tutorial.herokuapp.com/graphql")
    println("--------------------------------------------------------------------------------")

    val repository = RocketRepository()

    // Consultas a la API GraphQL Lauches todo en asíncrono
    println("Lista de lanzamientos")
    val lauches = launch(Dispatchers.IO) {
        val res = repository.getLaunchList()
        res.forEach { println("Get: $it") }
    }

    delay(500)
    // De esta manera nos quedamos conectados a la API GraphQL y recibimos los datos
    println("Lista de lanzamientos como un flujo")
    val lanzamientos = launch(Dispatchers.IO) {
        val res = repository.getLaunchListFlow()
        res.onStart { println("Comenzando a recibir datos") }
            .onCompletion { println("Finalizando la recepción de datos") }
            .onEach { println("Actualizando datos") }
            .collect { laucher ->
                laucher.forEach { println("Get Flow: $it") }
            }
    }

    delay(500)
    val res = repository.getLaunchById("5")
    println("Lanzamiento por ID")
    println("Lanzamiento por ID: $res")

    delay(500)

    println("Login")
    val login = async { RocketClient.login("pepe@miamil.es") }
    val token = login.await().toString()
    if (token.isNotEmpty()) {
        println("Login correcto con token: $token")
    } else {
        println("Login incorrecto")
    }

    delay(500)

    // Vamos a crear una suscripcion para que se ejecute cada vez que se actualice el lanzamiento
    println("Suscripción a un lanzamiento")
    val suscripcion = launch(Dispatchers.IO) {
        val res = repository.tripsBookedSubscription(token)
        res.onStart { println("Comenzando a recibir datos") }
            .onCompletion { println("Finalizando la recepción de datos") }
            .onEach { println("Actualizando datos") }
            .collect {
                when (val trips = it.data?.tripsBooked) {
                    null -> println("Error en la suscripción")
                    -1 -> println("-> Viaje cancelado")
                    else -> println("-> Viaje confirmado: $trips")
                }
            }
    }

    delay(500)
    println("Reservar un viaje o anular")
    if (res?.isBooked == true) {
        println("El lanzamiento ${res.id} con misión a ${res.mission?.name} está confirmado y lo anularemos")
        val anulacion = async { repository.cancelLaunch(res.id, token) }
        println("Anulación: ${anulacion.await()}")
    } else {
        println("El lanzamiento ${res?.id} con misión a ${res?.mission?.name} no esta confirmado y lo confirmaremos")
        val reserva = async { repository.bookLaunch(res!!.id, token) }
        println("Reserva: ${reserva.await()}")
    }

    delay(500)
    println("Reservar un viaje o anular")
    // Ahora vamos a a cambiar el lanzamiento
    var reservo = async { repository.bookLaunch(res!!.id, token) }
    println("Reserva ${res!!.id}: ${reservo.await()}")
    delay(100)
    reservo = async { repository.bookLaunch("93", token) }
    println("Reserva 93: ${reservo.await()}")

    delay(200)
    val cancelo = async { repository.cancelLaunch("93", token) }
    println("Anulación 93: ${cancelo.await()}")


    delay(2000)
    println("Finalizando")
    suscripcion.cancel()
    lanzamientos.cancel()
    lauches.cancel()
    exitProcess(0)

}