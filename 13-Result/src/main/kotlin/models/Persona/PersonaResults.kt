package models.Persona

// Lo mejor es hacerlo todo tipado y nos sirve para cualquier crud!!!
sealed interface PersonaResult<Persona> // sealed interface PersonaResult<T : Any>

// Tipamos el resultado de la operación, mejor con genéricos
class PersonaSuccess<T : Any>(val code: Int, val data: T) : PersonaResult<T>
//open class PersonaSuccess<Persona>(val data: Persona) : PersonaResult<Persona>

// Todos
// con genericos
// class PersonaSuccessAsList<T : Any>(data: List<T>) : PersonaSuccess<List<T>>(data)
//class PersonaSuccessAsList(data: List<Persona>) : PersonaSuccess<List<Persona>>(200, data)
//class PersonaSuccessAsFlow(data: Flow<Persona>) : PersonaSuccess<Flow<Persona>>(200, data)
//open class PersonaSuccessEntity(data: Persona) : PersonaSuccess<Persona>(200, data)

//class PersonaGetSuccess<T : Any>(data: T) : PersonaSuccess<T>(data)
// class PersonaSaveSuccess<T : Any>(data: T) : PersonaSuccess<T>(data)
// class PersonaDeleteByIdSuccess<T : Any>(data: T) : PersonaSuccess<T>(data)
//class PersonaDeleteSuccess<T : Any>(data: T) : PersonaSuccess<T>(data)

// Base de los errores, podemos poner codigos o solo mensajes, si no queremos que haya una le ponemos abstract
abstract class PersonaError<Nothing>(val code: Int, open val message: String?) : PersonaResult<Nothing>

// Clase base para el manejo de errores
class PersonaErrorNotFound<Nothing>(message: String?) : PersonaError<Nothing>(404, message)
class PersonasErrorBadRequest<Nothing>(message: String?) : PersonaError<Nothing>(400, message)
class PersonaInternalException<Nothing>(message: String?) : PersonaError<Nothing>(500, message)

// Clase base para el manejo de excepciones, si las hay la ponemos!!
// class PersonaException(val e: Throwable) : PersonaError<Nothing>(500, e.message)
