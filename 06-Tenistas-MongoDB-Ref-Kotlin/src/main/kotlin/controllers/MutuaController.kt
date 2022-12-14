package controllers

import dto.TenistaDto
import models.Raqueta
import models.Tenista
import mu.KotlinLogging
import repositories.tenistas.RaquetasRepository
import repositories.tenistas.TenistasRepositoryImpl

private val logger = KotlinLogging.logger {}

class MutuaController(
    private val tenistasRepository: TenistasRepositoryImpl,
    private val raquetasRepository: RaquetasRepository
) {

    // TENISTAS

    fun getTenistas(): List<Tenista> {
        logger.info("Obteniendo Tenistas")
        return tenistasRepository.findAll()
    }

    fun createTenista(tenista: Tenista): Tenista {
        logger.debug { "Creando tenista $tenista" }
        tenistasRepository.save(tenista)
        return tenista
    }

    fun getTenistaById(id: String): Tenista? {
        logger.debug { "Obteniendo tenista con id $id" }
        return tenistasRepository.findById(id)
    }

    fun updateTenista(tenista: Tenista) {
        logger.debug { "Updating tenista $tenista" }
        tenistasRepository.save(tenista)
    }

    fun deleteTenista(it: Tenista): Boolean {
        logger.debug { "Borrando tenista $it" }
        return tenistasRepository.delete(it)
    }

    fun getTenistasWithRaqueta(): List<TenistaDto> {
        logger.debug { "Obteniendo Tenistas con raqueta" }
        return tenistasRepository.findAllWithRaqueta()
    }

    // RAQUETAS
    fun getRaquetas(): List<Raqueta> {
        logger.info("Obteniendo Raquetas")
        return raquetasRepository.findAll()
    }

    fun createRaqueta(raqueta: Raqueta): Raqueta {
        logger.debug { "Creando raqueta $raqueta" }
        raquetasRepository.save(raqueta)
        return raqueta
    }

    fun getRaquetaById(id: String): Raqueta? {
        logger.debug { "Obteniendo raqueta con id $id" }
        return raquetasRepository.findById(id)
    }

    fun updateRaqueta(raqueta: Raqueta) {
        logger.debug { "Updating raqueta $raqueta" }
        raquetasRepository.save(raqueta)
    }

    fun deleteRaqueta(it: Raqueta): Boolean {
        logger.debug { "Borrando raqueta $it" }
        return raquetasRepository.delete(it)
    }
}