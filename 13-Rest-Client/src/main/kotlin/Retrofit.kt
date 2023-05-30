import dto.LoginDto
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.User
import repositories.RetrofitRepository
import services.retrofit.RetroApi
import kotlin.system.exitProcess

fun main(args: Array<String>): Unit = runBlocking {

    println()
    println("------------------------------------------------------")
    println("Retrofir: API REST Reqres.in - https://reqres.in/")
    println("Resources: Users")
    println("------------------------------------------------------")

    // Si quisieramos podemos usar async para que se ejecuten en paralelo
    val repository = RetrofitRepository()

    // Obtenemos todos los usuarios
    // No hace falta meterlo en una corrutina porque ya estoy en un run blovkin,
    // pero ya sabemos que no es recomendable
    val select = launch {
        println("Obtenemos todos los usuarios")
        repository.findAll(1, 100)
            .onStart { println("Comenzamos a obtener los usuarios") }
            .onCompletion { println("Fin de la consulta") }
            .collect {
                println(it)
            }

        // Mas sofisticado
        repository.findAll(1, 100).filter { it.firstName.first() == 'G' }
            .take(2)
            .collect {
                println(it)
            }
    }
    select.join()

    // Lo lanzo en paralelo con asyn await, si quisiera

    val getById1 = async { repository.findById(3) }
    val getById2 = async { repository.findById(5) }
    println("Get by ID: ${getById1.await()}")
    println("Get by ID: ${getById2.await()}")


    // Todo en paralelo, porque en el fondo está congelada
    val user = User(
        firstName = "George",
        lastName = "Bluth",
        avatar = "https://s3.amazonaws.com/uifaces/faces/twitter/calebogden/128.jpg",
        email = "test@example.com",
    )

    // Podemos usar asyn y await tambien
    println("Create user")
    var res = repository.save(user)
    println("Create: $res")

    // Actualizamos el usuario
    println("Update user")
    res = res.copy(firstName = "Janet", email = "updted@updted.com")
    res = repository.update(res)
    println("Update: $res")

    // Eliminamos el usuario
    println("Delete user")
    res = repository.delete(res)
    println("Delete: $res")

    // Nos autenticamos
    println("Login")
    val login = async { RetroApi.login(LoginDto("eve.holt@reqres.in", "cityslicka")) }
    val token = login.await()
    if (token.isNotEmpty()) {
        println("Login correcto con token: $token")
    } else {
        println("Login incorrecto")
    }

    println("Obtenemos todos los usuarios con token")
    repository.findAllWithToken(token, 1, 100)
        .onStart { println("Comenzamos a obtener los usuarios") }
        .onCompletion { println("Fin de la consulta") }
        .collect {
            println(it)
        }

    exitProcess(0)
}



