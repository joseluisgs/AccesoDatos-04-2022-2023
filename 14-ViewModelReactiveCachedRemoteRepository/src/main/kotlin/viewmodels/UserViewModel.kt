package viewmodels

import com.github.michaelbull.result.*
import errors.UserError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import models.User
import mu.KotlinLogging
import org.koin.core.annotation.Singleton
import repositories.UsersRepository
import validator.validate

private val logger = KotlinLogging.logger {}
private const val REFRESH_INTERVAL = 10 * 1000L // 1 minuto

@Singleton
class UserViewModel(
    val repository: UsersRepository
) {

    // Mi estado!!!!
    private val _state = MutableStateFlow(MyState())
    val state: StateFlow<MyState> = _state

    init {
        logger.info { "UserViewModel.init()" }
        refreshData()
        updateStateData()
    }

    private fun updateStateData() {
        logger.info { "UserViewModel.updateStateData()" }
        // Como la base de datos es un Flow, cada vez que cambie, se actualiza el estado
        CoroutineScope(Dispatchers.IO).launch {
            repository.getAll().collect { users ->
                _state.value = _state.value.copy(users = users, isLoading = false)
            }
        }
    }

    private fun refreshData() {
        // Podemos programar que se refresque cada cierto tiempo
        CoroutineScope(Dispatchers.IO).launch {
            //do {
            logger.info { "UserViewModel.refreshData() - Refreshing data" }
            _state.value = _state.value.copy(isLoading = true)
            repository.refresh().onSuccess {
                _state.value = _state.value.copy(
                    users = repository.getAll().first(),
                    isLoading = false
                )
            }.onFailure {
                println("ERROR refresh ${it.message}")
            }

            //delay(REFRESH_INTERVAL)
            // } while (true)
        }
    }

    suspend fun getAllUsers() = repository.getAll()

    suspend fun getUserById(id: Long) = repository.getById(id)

    suspend fun saveUser(user: User): Result<User, UserError> {
        // Railway programming encadenas acciones
        return user.validate().andThen {
            _state.value = _state.value.copy(isLoading = true)
            // El estado ya se cambia porque nos hemos suscrito a los cambios de la base de datos
            repository.save(it).andThen { savedUser ->
                Ok(savedUser)
            }
        }
    }


    suspend fun updateUser(user: User): Result<User, UserError> {
        return repository.getById(user.id).andThen {
            user.validate().andThen {
                _state.value = _state.value.copy(isLoading = true)
                // El estado ya se cambia porque nos hemos suscrito a los cambios de la base de datos
                repository.save(it).andThen { savedUser ->
                    Ok(savedUser)
                }
            }
        }
    }

    suspend fun deleteUser(id: Long): Result<User, UserError> {
        return repository.getById(id).andThen {
            _state.value = _state.value.copy(isLoading = true)
            // El estado ya se cambia porque nos hemos suscrito a los cambios de la base de datos
            repository.delete(it).andThen { savedUser ->
                Ok(savedUser)
            }
        }
    }


    data class MyState(
        val users: List<User> = emptyList(),
        val isLoading: Boolean = false,
    )
}