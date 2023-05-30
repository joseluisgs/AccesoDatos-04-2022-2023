package models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long = 0,
    @SerialName("first_name")
    var firstName: String,
    @SerialName("last_name")
    var lastName: String,
    var avatar: String,
    var email: String
)