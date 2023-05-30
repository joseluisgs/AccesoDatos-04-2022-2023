package errors

sealed class UserError(val message: String) {
    class NotFound(message: String) : UserError(message)
    class CreateUser(message: String) : UserError(message)
    class UpdateUser(message: String) : UserError(message)
    class DeleteUser(message: String) : UserError(message)
    class RefreshUsers(message: String) : UserError(message)
    class BadRequest(message: String) : UserError(message)

}