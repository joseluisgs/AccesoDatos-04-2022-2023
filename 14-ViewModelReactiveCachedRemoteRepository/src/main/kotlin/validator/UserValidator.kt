package validator

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import errors.UserError
import models.User
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun User.validate(): Result<User, UserError> {

    logger.debug { "User.validate() $this" }

    val errors = mutableListOf<String>()

    // Comenzamos a validar los campos
    if (name.isBlank()) {
        errors.add("Name cannot be blank")
    }
    if (username.isBlank()) {
        errors.add("Username cannot be blank")
    }
    // regular expression to validate email
    if (email.isBlank() || !email.matches(Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"))) {
        errors.add("Email cannot be blank or invalid")
    }
    if (phone.isBlank()) {
        errors.add("Phone cannot be blank")
    }
    if (website.isBlank()) {
        errors.add("Website cannot be blank")
    }

    // Si hay errores, devolvemos un error
    return if (errors.isNotEmpty()) {
        Err(UserError.BadRequest(errors.joinToString(", ")))
    } else {
        Ok(this)
    }

}