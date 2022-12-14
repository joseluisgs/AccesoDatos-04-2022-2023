package es.joseluisgs.encordadosmongodbreactivespringdatakotlin.repositories

import es.joseluisgs.encordadosmongodbreactivespringdatakotlin.models.Tenista
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TenistasRepository : CoroutineCrudRepository<Tenista, ObjectId> {
    fun findByNombre(nombre: String): Flow<Tenista>
}