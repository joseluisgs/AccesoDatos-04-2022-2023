package dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginDto(
    var email: String,
    var password: String
)
