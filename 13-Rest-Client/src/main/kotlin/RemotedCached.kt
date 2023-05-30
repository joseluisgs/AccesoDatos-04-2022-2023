import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.User
import repositories.RemoteCachedRepository
import services.sqldelight.SqlDeLight

fun main(): Unit = runBlocking {
    println("Remote - Cached - Repository")

    SqlDeLight.removeAllData()

    val repository = RemoteCachedRepository()

    // Lanzamos el refresh
    val refresh = launch {
        repository.refresh()
    }

    delay(1000)


    val listener = launch {
        println("Find All")
        // Si lo devolvemos como un flujo se queda enganchado para para escuchar cualquier campo
        // Me suscribo solo con los cambios distintos o si lo quito obtengo todo de nuevo
        repository.findAll()
            .onStart { println("✔ Obteniendo usuarios") }
            .onEach { println("🔷 Obteniendo usuarios actualizados") }
            .collect { println("\uD83D\uDC49 Usuarios: $it") }
        // repository.findAll().distinctUntilChanged().collect { users -> users.forEach { println("Get: $it") } }
    }

    delay(1000)
    println("Find by Id")
    var user = repository.findById(10)
    println("GetById: $user")

    delay(1000)
    println("Insert")
    user = User(
        id = 0,
        firstName = "Test",
        lastName = "test",
        email = "test@example.com",
        avatar = "https://example.com/test.jpg"
    )
    user = repository.save(user)
    println("Insert: $user")

    delay(1000)
    user.apply {
        firstName = "Test 2"
        lastName = "test 2"
        email = "testing2@example.com"
        avatar = "https://example.com/test2.jpg"
    }
    println("Update")
    user = repository.update(user)
    println("Update: $user")

    delay(1000)
    println("Delete")
    user = repository.delete(user)
    println("Delete: $user")

    delay(1000)
    refresh.cancel()
    listener.cancel()


}