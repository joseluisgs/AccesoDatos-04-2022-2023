package dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import models.User

@Serializable
data class GetAllDto(
    val page: Int = 0,
    @SerialName("per_page")
    val perPage: Int = 0,
    val total: Int = 0,
    @SerialName("total_pages")
    val totalPages: Int = 0,
    val data: List<User>,
    val support: SupportDto,
)

@Serializable
data class GetByIdDto(
    val data: User,
    val support: SupportDto,
)

@Serializable
data class CreateDto(
    val id: Long = 0,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    val avatar: String,
    val email: String,
    val createdAt: String
)

@Serializable
data class UpdateDto(
    val id: Long = 0,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    val avatar: String,
    val email: String,
    val updatedAt: String
)


@Serializable
data class SupportDto(
    val url: String,
    val text: String
)

@Serializable
data class TokenDto(
    val token: String
)

