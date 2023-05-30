package db

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import mu.KotlinLogging
import org.bson.Document
import org.bson.conversions.Bson
import org.litote.kmongo.KMongo
import org.litote.kmongo.aggregate

private val logger = KotlinLogging.logger {}


object MongoDbManager {
    private lateinit var mongoClient: MongoClient
    lateinit var database: MongoDatabase

    init {
        logger.debug("Inicializando conexion a MongoDB")
        // Aplicamos Hiraki para la conexión a la base de datos
        mongoClient = KMongo.createClient("mongodb://mongoadmin:mongopass@localhost/tenistas?authSource=admin")
        database = mongoClient.getDatabase("tenistas")
    }
}

/**
 * Función de extensión para realizar un lookup (join) en una colección de MongoDB
 * @param otherCollection Nombre de la colección a la que se hace referencia
 * @param localField Campo de la colección actual
 * @param otherField Campo de la colección a la que se hace referencia para hacer el lookup o join
 * @param asField Campo que se crea en el documento con el resultado del lookup
 * @param mapper Función que se aplica al resultado del lookup para transformarlo en el tipo de dato deseado
 */
inline fun <reified T, reified D> MongoCollection<T>.lookup(
    otherCollection: String,
    localField: String,
    otherField: String,
    asField: String,
    crossinline mapper: (Document) -> D
): List<D> {
    // val json: Json = Json { ignoreUnknownKeys = true }
    val pipeline: Bson = org.litote.kmongo.lookup(otherCollection, localField, otherField, asField)
    // si quieres puede sponer el serializador aquí
    // return this.aggregate<Document>(pipeline).map { json.decodeFromString<D>(it.toJson()) }.toList()
    return this.aggregate<Document>(pipeline).map { mapper(it) }.toList()
}
