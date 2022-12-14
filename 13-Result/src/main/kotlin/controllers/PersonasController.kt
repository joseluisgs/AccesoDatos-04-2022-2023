package controllers

import kotlinx.coroutines.flow.Flow
import models.Persona.*
import repositories.personas.PersonasRepository
import java.util.*

class PersonasController(private val repository: PersonasRepository) {
    suspend fun getAllAsFlow(): Flow<List<Persona>> {
        return repository.findAllAsFlow()
    }

    fun getAll(): PersonaResponse<out List<Persona>> {
        val result = repository.findAll()
        return when (result) {
            is PersonaSuccess -> PersonaResponseSuccess(result.code, result.data)
            is PersonaErrorNotFound -> PersonaResponseError(result.code, result.message!!)
            is PersonaInternalException -> PersonaResponseError(result.code, result.message!!)
            else -> PersonaResponseError(400, "Error desconocido al obtener todas las personas")
        }
    }

    fun getById(id: UUID): PersonaResponse<out Persona> {
        val result = repository.getById(id)
        return when (result) {
            is PersonaSuccess -> PersonaResponseSuccess(result.code, result.data)
            is PersonaErrorNotFound -> PersonaResponseError(result.code, result.message!!)
            is PersonaInternalException -> PersonaResponseError(result.code, result.message!!)
            else -> PersonaResponseError(400, "Error desconocido al obtener todas las personas")
        }
    }

    fun getByNombre(nombre: String): PersonaResponse<out List<Persona>> {
        val result = repository.getByNombre(nombre)
        return when (result) {
            is PersonaSuccess -> PersonaResponseSuccess(result.code, result.data)
            is PersonaErrorNotFound -> PersonaResponseError(result.code, result.message!!)
            is PersonaInternalException -> PersonaResponseError(result.code, result.message!!)
            else -> PersonaResponseError(400, "Error desconocido al obtener por nombre")
        }
    }

    fun save(entity: Persona): PersonaResponse<out Persona> {
        val result = repository.save(entity)
        return when (result) {
            is PersonaSuccess -> PersonaResponseSuccess(result.code, result.data)
            is PersonaErrorNotFound -> PersonaResponseError(result.code, result.message!!)
            is PersonaInternalException -> PersonaResponseError(result.code, result.message!!)
            else -> PersonaResponseError(400, "Error desconocido al salvar personas")
        }
    }

    fun update(entity: Persona): PersonaResponse<out Persona> {
        val result = repository.update(entity)
        return when (result) {
            is PersonaSuccess -> PersonaResponseSuccess(result.code, result.data)
            is PersonasErrorBadRequest -> PersonaResponseError(result.code, result.message!!)
            is PersonaErrorNotFound -> PersonaResponseError(result.code, result.message!!)
            is PersonaInternalException -> PersonaResponseError(result.code, result.message!!)
            else -> PersonaResponseError(400, "Error desconocido al actualizar personas")
        }
    }

    fun deleteById(id: UUID): PersonaResponse<out Persona> {
        val result = repository.deleteById(id)
        return when (result) {
            is PersonaSuccess -> PersonaResponseSuccess(result.code, result.data)
            is PersonaErrorNotFound -> PersonaResponseError(result.code, result.message!!)
            is PersonaInternalException -> PersonaResponseError(result.code, result.message!!)
            else -> PersonaResponseError(400, "Error desconocido al eliminar personas")
        }
    }

    fun delete(entity: Persona): PersonaResponse<out Persona> {
        val result = repository.delete(entity)
        return when (result) {
            is PersonaSuccess -> PersonaResponseSuccess(result.code, result.data)
            is PersonaErrorNotFound -> PersonaResponseError(result.code, result.message!!)
            is PersonaInternalException -> PersonaResponseError(result.code, result.message!!)
            else -> PersonaResponseError(400, "Error desconocido al eliminar personas")
        }
    }

}