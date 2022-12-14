package repositories.personas

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import models.Persona.*
import java.util.*

class PersonasRepositoryImp : PersonasRepository {
    private val personas = mutableMapOf<UUID, Persona>()

    override suspend fun findAllAsFlow() = flow {
        do {
            emit(personas.values.toList())
            delay(1000)
        } while (true)
    }

    override fun findAll(): PersonaResult<List<Persona>> {
        return if (personas.isEmpty()) {
            PersonaErrorNotFound("No hay personas registradas")
        } else {
            PersonaSuccess(200, personas.values.toList())
        }
    }

    override fun getById(id: UUID): PersonaResult<Persona> {
        return personas[id]?.let { PersonaSuccess(200, it) }
            ?: PersonaErrorNotFound("No se encontró la persona con el id $id")
    }

    override fun getByNombre(nombre: String): PersonaResult<List<Persona>> {
        return if (personas.isEmpty()) {
            PersonaErrorNotFound("No hay personas registradas")
        } else {
            val personasFiltradas = personas.values.filter { it.nombre == nombre }
            if (personasFiltradas.isEmpty()) {
                PersonaErrorNotFound("No se encontró la persona con el nombre $nombre")
            } else {
                PersonaSuccess(200, personasFiltradas)
            }
        }
    }

    override fun save(entity: Persona): PersonaResult<Persona> {
        // comprobamos los campos
        if (entity.nombre.isBlank()) {
            return PersonasErrorBadRequest("El nombre no puede estar vacío")
        }
        if (entity.edad < 0) {
            return PersonasErrorBadRequest("La edad no puede ser negativa")
        }
        personas[entity.uuid] = entity
        return PersonaSuccess(201, entity)
    }

    override fun update(entity: Persona): PersonaResult<Persona> {
        // existe la persona
        if (personas[entity.uuid] == null) {
            return PersonaErrorNotFound("No se encontró la persona con el id ${entity.uuid}")
        }
        // comprobamos los campos
        if (entity.nombre.isBlank()) {
            return PersonasErrorBadRequest("El nombre no puede estar vacío")
        }
        if (entity.edad < 0) {
            return PersonasErrorBadRequest("La edad no puede ser negativa")
        }
        personas[entity.uuid] = entity
        return PersonaSuccess(200, entity)
    }


    override fun deleteById(id: UUID): PersonaResult<Persona> {
        // el id no existe
        if (!personas.containsKey(id)) {
            return PersonaErrorNotFound("No se encontró la persona con el id $id")
        }
        val persona = personas[id]!!
        personas.remove(id)
        return PersonaSuccess(200, persona)
    }

    override fun delete(entity: Persona): PersonaResult<Persona> {
        // el id no existe
        if (!personas.containsKey(entity.uuid)) {
            return PersonaErrorNotFound("No se encontró la persona con el id ${entity.uuid}")
        }
        personas.remove(entity.uuid)
        return PersonaSuccess(200, entity)
    }
}
