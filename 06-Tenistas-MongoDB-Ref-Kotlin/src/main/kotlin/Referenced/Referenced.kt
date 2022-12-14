package Referenced

import db.MongoDbManager
import models.Representante
import mu.KotlinLogging
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.conversions.Bson
import org.litote.kmongo.*

private val logger = KotlinLogging.logger {}

// Filosofía Referenced, cada propblema tiene su solución
// Ni la solución es siempre embeded, ni la solución es siempre referenced

data class Raqueta(
    @BsonId
    val id: String = newId<Raqueta>().toString(),
    var marca: String,
    var precio: Double,
    var represetante: Representante? = null
)

data class Representante(
    @BsonId
    val id: String = newId<Representante>().toString(),
    var nombre: String,
    var email: String
)

data class Tenista
    (
    @BsonId
    val id: String = newId<Tenista>().toString(),
    val nombre: String,
    var raqueta_id: String? = null // Referencia
)

// Ensamblo los datos
data class TenistaDto(
    @BsonId
    val id: String,
    val nombre: String,
    val raqueta: Raqueta?
)

fun main(args: Array<String>) {
    logger.debug { "Borrando colecciones" }
    if (MongoDbManager.database.getCollection<models.Tenista>().countDocuments() > 0) {
        MongoDbManager.database.getCollection<models.Tenista>().drop()
    }
    if (MongoDbManager.database.getCollection<Raqueta>().countDocuments() > 0) {
        MongoDbManager.database.getCollection<Raqueta>().drop()
    }

    // creo un representante
    val representante = Representante(nombre = "Representante", email = "representante@gmail.com")

    // creo una raqueta
    val raqueta = Raqueta(newId<Raqueta>().toString(), "Wilson", 100.0, representante)
    // la inserto
    MongoDbManager.database.getCollection<Raqueta>().save(raqueta)
    // recorro la colección de raquetas
    MongoDbManager.database.getCollection<Raqueta>().find().forEach { println(it) }

    // creo un tenista
    var tenista = Tenista(newId<Tenista>().toString(), "Rafael Nadal", raqueta.id)
    // la inserto
    MongoDbManager.database.getCollection<Tenista>().save(tenista)
    // recorro la colección de tenistas
    MongoDbManager.database.getCollection<Tenista>().find().forEach { println(it) }

    // Jugamos con los agregados
    tenista = MongoDbManager.database.getCollection<Tenista>()
        .aggregate<Tenista>(
            match(Tenista::nombre regex "ael"),
            sample(1)
        ).first() ?: throw Exception("No hay tenistas con ese nombre")
    println(tenista)

    // Looookup, recuerda que un Document lo puedes pasar a Json y deserializarlo!!!
    MongoDbManager.database.getCollection<Tenista>()
    var pipeline: Bson = lookup("raqueta", "raqueta_id", "_id", "mi_raqueta")
    val result = MongoDbManager.database.getCollection<Tenista>()
        .aggregate<Document>(pipeline).map {
            // Parece más lioso porque hago el mapper aquí, pero si no es un mapper de Document a la Entidad
            logger.debug { "Document Tenista: $it" }
            TenistaDto(
                it.getString("_id"),
                it.getString("nombre"), // siempre se recupera una lista!!! aunqeu solo haya objeto o esté vacía
                it.get("mi_raqueta", List::class.java).firstOrNull()?.let { raqueta ->
                    raqueta as Document
                    logger.debug { "Document Raqueta: $raqueta" }
                    val representante = raqueta.get("represetante", Document::class.java)
                    logger.debug { "Representante Document: $representante" }
                    Raqueta(
                        raqueta.getString("_id"),
                        raqueta.getString("marca"),
                        raqueta.getDouble("precio"),
                        representante?.let {
                            Representante(
                                representante.getString("_id"),
                                representante.getString("nombre"),
                                representante.getString("email")
                            )
                        }
                    )
                }
            )
        }.toList()
    println(result)

    // Lo mismo pero con el mapper de KMongo
    pipeline = lookup("tenista", "_id", "raqueta_id", "mis_tenistas")
    val result2 = MongoDbManager.database.getCollection<Raqueta>()
        .aggregate<Document>(pipeline).toList()
    println(result2)

}