import models.User
import services.sqldelight.SqlDeLightClient

fun main() {
    println("SQLDelight")

    println(SqlDeLightClient.queries.selectUsers().executeAsList())

    val user = User(
        first_name = "George",
        last_name = "Bluth",
        avatar = "https://s3.amazonaws.com/uifaces/faces/twitter/calebogden/128.jpg",
        email = "test@example.com"
    )

    SqlDeLightClient.queries.insert(
        first_name = user.first_name,
        last_name = user.last_name,
        avatar = user.avatar,
        email = user.email
    )

    println(SqlDeLightClient.queries.selectUsers().executeAsList())

    user.apply {
        first_name = "George2"
        last_name = "Bluth2"
        avatar = "https://s3.amazonaws.com/uifaces/faces/twitter/calebogden/128.jpg"
        email = "test@bluth2.com"
    }

    SqlDeLightClient.queries.update(
        first_name = user.first_name,
        last_name = user.last_name,
        avatar = user.avatar,
        email = user.email,
        id = 2
    )

    println(SqlDeLightClient.queries.selectUsers().executeAsList())

    SqlDeLightClient.queries.delete(2)

    println(SqlDeLightClient.queries.selectUsers().executeAsList())

    // Puedo usar Mapper para convertir de un objeto a otro
    // o crearme mi propio mapper, lo verás más adelante
    val res = SqlDeLightClient.queries.selectById(1, mapper =
    { id: Long, first_name: String, last_name: String, avatar: String?, email: String? ->
        User(id.toLong(), first_name, last_name, avatar, email)
    }).executeAsOne()
    println(res)
}