package models.Result


//sealed interface ResultOtro<T : Any>
//
//class SuccessOtro<T : Any>(val data: T) : ResultOtro<T>
//class ErrorOtro<T : Any>(val code: Int, val message: String?) : ResultOtro<T>
//class ExceptionOtro<T : Any>(val e: Throwable) : ResultOtro<T>

// Clase base para el manejo de errores
// es un monad que puede ser un valor o un error
// o hacerlo con solo uno
//sealed class Result<out Success, out Error>
//data class Success<out Success>(val value: Success) : Result<Success, Nothing>()
//data class Failure<out Failure>(val code: Int, val message: String?) : Result<Nothing, Failure>()
//data class Exception<out Throwable>(val reason: Throwable) : Result<Nothing, Throwable>()


