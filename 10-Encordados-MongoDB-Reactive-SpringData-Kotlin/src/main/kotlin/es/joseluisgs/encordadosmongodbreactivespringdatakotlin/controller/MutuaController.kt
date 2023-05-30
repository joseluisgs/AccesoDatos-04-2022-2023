package es.joseluisgs.encordadosmongodbreactivespringdatakotlin.controller

import es.joseluisgs.encordadosmongodbreactivespringdatakotlin.models.Raqueta
import es.joseluisgs.encordadosmongodbreactivespringdatakotlin.models.Tenista
import es.joseluisgs.encordadosmongodbreactivespringdatakotlin.repositories.RaquetasRepository
import es.joseluisgs.encordadosmongodbreactivespringdatakotlin.repositories.TenistasRepository
import es.joseluisgs.encordadosmongodbreactivespringdatakotlin.services.RaquetasService
import es.joseluisgs.encordadosmongodbreactivespringdatakotlin.services.TenistasService
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ChangeStreamEvent
import org.springframework.stereotype.Controller


private val logger = KotlinLogging.logger {}

@Controller
class MutuaController
@Autowired constructor(
    private val raquetasRepository: RaquetasRepository,
    private val tenistasRepository: TenistasRepository,
    private val raquetasService: RaquetasService,
    private val tenistasService: TenistasService,
) {

    // TENISTAS


    // Tenistas
    suspend fun tenistasDeleteAll() {
        logger.info("Borrando todos los tenistas")
        tenistasRepository.deleteAll()
    }

    fun getTenistas(): Flow<Tenista> {
        logger.info("Obteniendo Tenistas")
        return tenistasRepository.findAll()
    }

    fun watchTenistas(): Flow<ChangeStreamEvent<Tenista>> {
        logger.info("getTenistasAndWatch")
        return tenistasService.watch()
    }

    suspend fun createTenista(tenista: Tenista): Tenista {
        logger.debug { "Creando tenista $tenista" }
        tenistasRepository.save(tenista)
        return tenista
    }

    suspend fun getTenistaById(id: ObjectId): Tenista? {
        logger.debug { "Obteniendo tenista con uuid $id" }
        return tenistasRepository.findById(id) ?: throw Exception("No existe el tenista con id $id")
    }

    suspend fun updateTenista(tenista: Tenista) {
        logger.debug { "Updating tenista $tenista" }
        tenistasRepository.save(tenista)
    }

    suspend fun deleteTenista(tenista: Tenista): Boolean {
        logger.debug { "Borrando tenista $tenista" }
        tenistasRepository.delete(tenista)
        return true
    }

    // RAQUETAS
    suspend fun raquetasDeleteAll() {
        logger.info("Borrando todas los raquetas")
        raquetasRepository.deleteAll()
    }

    fun getRaquetas(): Flow<Raqueta> {
        logger.info("Obteniendo Raquetas")
        return raquetasRepository.findAll()
    }

    fun watchRaquetas(): Flow<ChangeStreamEvent<Raqueta>> {
        logger.info("Obteniendo Raquetas y escuchando cambios")
        return raquetasService.watch()
    }

    suspend fun createRaqueta(raqueta: Raqueta): Raqueta {
        logger.debug { "Creando raqueta $raqueta" }
        raquetasRepository.save(raqueta)
        return raqueta
    }

    suspend fun getRaquetaById(id: ObjectId): Raqueta? {
        logger.debug { "Obteniendo raqueta con uuid $id" }
        return raquetasRepository.findById(id) ?: throw Exception("No existe la raqueta con id $id")
    }

    suspend fun updateRaqueta(raqueta: Raqueta): Raqueta {
        logger.debug { "Actualizando $raqueta" }
        return raquetasRepository.save(raqueta)
    }

    suspend fun deleteRaqueta(raqueta: Raqueta): Boolean {
        logger.debug { "Borrando raqueta $raqueta" }
        raquetasRepository.delete(raqueta)
        return true
    }

    fun findRaquetaByMarca(marca: String): Flow<Raqueta> {
        logger.debug { "Buscando raqueta por marca $marca" }
        return raquetasRepository.findByMarca(marca)
    }
}