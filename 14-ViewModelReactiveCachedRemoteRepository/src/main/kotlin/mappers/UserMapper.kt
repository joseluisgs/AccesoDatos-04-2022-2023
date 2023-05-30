package mappers

import dto.placeholder.UserDto
import database.User as UserEntity
import models.User as UserModel

fun UserDto.toModel() = UserModel(
    id = id,
    name = name,
    username = username,
    email = email,
    phone = phone,
    website = website
)

@JvmName("toModelUserDto")
fun List<UserDto>.toModel() = this.map { it.toModel() }

fun UserModel.toDto() = UserDto(
    id = id,
    name = name,
    username = username,
    email = email,
    phone = phone,
    website = website
)

fun UserEntity.toModel() = UserModel(
    id = id,
    name = name,
    username = username,
    email = email,
    phone = phone,
    website = website
)

@JvmName("toModelUserEntity")
fun List<UserEntity>.toModel() = this.map { it.toModel() }

fun UserModel.toEntity() = UserEntity(
    id = id,
    name = name,
    username = username,
    email = email,
    phone = phone,
    website = website
)