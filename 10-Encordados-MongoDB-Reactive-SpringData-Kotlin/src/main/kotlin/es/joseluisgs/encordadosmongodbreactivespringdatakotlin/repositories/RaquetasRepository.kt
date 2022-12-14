package es.joseluisgs.encordadosmongodbreactivespringdatakotlin.repositories

import es.joseluisgs.encordadosmongodbreactivespringdatakotlin.models.Raqueta
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository


@Repository
interface RaquetasRepository : CoroutineCrudRepository<Raqueta, ObjectId> {
    fun findByMarca(marca: String): Flow<Raqueta>
}