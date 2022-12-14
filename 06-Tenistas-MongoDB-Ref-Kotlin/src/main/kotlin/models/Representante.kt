package models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.newId

@Serializable
data class Representante(
    @BsonId @Contextual
    val id: String = newId<Representante>().toString(),
    var nombre: String,
    var email: String
)