import models.User
import services.sqldelight.SqlDeLight

fun main() {
    println("SQLDelight")

    println(SqlDeLight.client.selectUsers().executeAsList())

    val user = User(
        firstName = "George",
        lastName = "Bluth",
        avatar = "https://s3.amazonaws.com/uifaces/faces/twitter/calebogden/128.jpg",
        email = "test@example.com"
    )

    SqlDeLight.client.insert(
        first_name = user.firstName,
        last_name = user.lastName,
        avatar = user.avatar,
        email = user.email
    )

    println(SqlDeLight.client.selectUsers().executeAsList())

    user.apply {
        firstName = "George2"
        lastName = "Bluth2"
        avatar = "https://s3.amazonaws.com/uifaces/faces/twitter/calebogden/128.jpg"
        email = "test@bluth2.com"
    }

    SqlDeLight.client.update(
        first_name = user.firstName,
        last_name = user.lastName,
        avatar = user.avatar,
        email = user.email,
        id = 2
    )

    println(SqlDeLight.client.selectUsers().executeAsList())

    SqlDeLight.client.delete(2)

    println(SqlDeLight.client.selectUsers().executeAsList())

    // Puedo usar Mapper para convertir de un objeto a otro
    // o crearme mi propio mapper, lo verás más adelante
    val res = SqlDeLight.client.selectById(1, mapper =
    { id: Long, first_name: String, last_name: String, avatar: String, email: String ->
        User(id.toLong(), first_name, last_name, avatar, email)
    }).executeAsOne()
    
}