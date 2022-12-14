package mappers

// Vamos a usar alias para imports
import database.User as UserEntity
import models.User as UserModel


fun UserEntity.toUserModel(): UserModel {
    return UserModel(
        id = id,
        first_name = first_name,
        last_name = last_name,
        email = email,
        avatar = avatar
    )
}

fun UserModel.toUserEntity(): UserEntity {
    return UserEntity(
        id = id,
        first_name = first_name,
        last_name = last_name,
        email = email,
        avatar = avatar
    )
}
