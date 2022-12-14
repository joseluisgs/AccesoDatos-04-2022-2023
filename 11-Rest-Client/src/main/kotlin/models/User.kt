package models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long = 0,
    var first_name: String,
    var last_name: String,
    var avatar: String? = null,
    var email: String? = null,
)