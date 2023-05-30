import com.github.michaelbull.result.fold
import di.mainModule
import errors.UserError
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import models.User
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.fileProperties
import org.koin.logger.slf4jLogger
import viewmodels.UserViewModel

fun main(args: Array<String>) {
    // Iniciamos Koin
    startKoin {
        // looging
        slf4jLogger()
        // Leemos propiedades
        fileProperties("/application.properties")
        // declare used modules
        modules(mainModule)
        //defaultModule()
    }

    App().run()
}

class App : KoinComponent {
    /*private val viewModel = UserViewModel(
        UsersRepositoryImpl(
            remote = KtorFitClient(),
            local = SqlDeLightClient("jdbc:sqlite:", "true")
        )
    )
*/
    // val databaseUrl = getKoin().getProperty("database.url", "jdbc:sqlite:")

    private val viewModel by inject<UserViewModel>()

    fun run() = runBlocking {
        //println("Database URL: $databaseUrl")

        // Ahora solo escuchamos los cambios en el estado
        val job = CoroutineScope(Dispatchers.IO).launch {
            viewModel.state
                .onStart { println("\uD83E\uDDD0 Escuchando cambios en el estado") }
                .onEach { println("\uD83D\uDCA5 Nuevo estado") }
                .collect {
                    if (it.isLoading) {
                        // Pitamos el reloj de arena
                        println("\uD83D\uDD04 Loading...")
                    } else {
                        // Desaparece el reloj de arena
                        // Pintamos los usuarios
                        println("Lista \uD83D\uDC65 ${it.users}")
                    }
                }
        }

        delay(1000)
        // Ahora solo escuchamos los cambios en el estado y a jugar
        println()
        println("Consultamos los datos locales")
        viewModel.getAllUsers().first().forEach { println("\uD83D\uDC65 $it") }

        delay(1000)
        println()
        println("Consultamos los datos del usuario con id 1")
        viewModel.getUserById(1).fold(
            success = { println("\uD83D\uDC65 $it") },
            failure = { handleErrors(it) }
        )

        delay(1000)
        println()
        println("Consultamos los datos del usuario con id -1")
        viewModel.getUserById(-1).fold(
            success = { println("\uD83D\uDC65 $it") },
            failure = { handleErrors(it) }
        )

        delay(1000)
        println()
        println("Creamos un nuevo usuario")
        var newUser = User(User.NEW_USER_ID, "test new user", "new", "email@email.com", "123", "http://pepe.com")
        viewModel.saveUser(newUser).fold(
            success = { println("Nuevo \uD83D\uDC64 $it") },
            failure = { handleErrors(it) }
        )

        delay(1000)
        println()
        println("Creamos un nuevo usuario con problemas de validación")
        newUser = User(User.NEW_USER_ID, "test new user", "new", "email@email", "123", "")
        viewModel.saveUser(newUser).fold(
            success = { println("Nuevo \uD83D\uDC64 $it") },
            failure = { handleErrors(it) }
        )

        delay(1000)
        println()
        println("Modificamos el usuario con id 1")
        var updatedUser = User(1, "test update user", "updated", "email@email.com", "123", "http://pepe.com")
        viewModel.updateUser(updatedUser).fold(
            success = { println("Actualizado \uD83D\uDC64 $it") },
            failure = { handleErrors(it) }
        )

        delay(1000)
        println()
        println("Modificamos el usuario con id -1")
        updatedUser = User(99, "test update user", "updated", "email@email.com", "123", "http://pepe.com")
        viewModel.updateUser(updatedUser).fold(
            success = { println("Actualizado \uD83D\uDC64 $it") },
            failure = { handleErrors(it) }
        )

        delay(1000)
        println()
        println("Borramos el usuario con id 1")
        viewModel.deleteUser(1).fold(
            success = { println("Borrado \uD83D\uDC64 $it") },
            failure = { handleErrors(it) }
        )

        // No se cierra el programa
        delay(1000)
        job.cancelAndJoin()
    }

    private fun handleErrors(it: UserError) {
        when (it) {
            is UserError.BadRequest -> println("ERROR Validación: \uD83D\uDEAB ${it.message}")
            is UserError.CreateUser -> println("ERROR Creación: \uD83D\uDEAB ${it.message}")
            is UserError.DeleteUser -> println("ERROR Borrado: \uD83D\uDEAB ${it.message}")
            is UserError.NotFound -> println("ERROR No encontrado: \uD83D\uDEAB ${it.message}")
            is UserError.RefreshUsers -> println("ERROR Refresco: \uD83D\uDEAB ${it.message}")
            is UserError.UpdateUser -> println("ERROR Actualización: \uD83D\uDEAB ${it.message}")
        }
    }
}
