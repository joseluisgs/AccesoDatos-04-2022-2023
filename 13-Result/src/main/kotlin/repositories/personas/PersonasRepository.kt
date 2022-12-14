package repositories.personas

import kotlinx.coroutines.flow.Flow
import models.Persona.Persona
import models.Persona.PersonaResult
import java.util.*

interface PersonasRepository {
    suspend fun findAllAsFlow(): Flow<List<Persona>> // Para tiempo real!!!
    fun findAll(): PersonaResult<List<Persona>>
    fun getById(id: UUID): PersonaResult<Persona>
    fun getByNombre(nombre: String): PersonaResult<List<Persona>>
    fun save(entity: Persona): PersonaResult<Persona>
    fun update(entity: Persona): PersonaResult<Persona>
    fun deleteById(id: UUID): PersonaResult<Persona>
    fun delete(entity: Persona): PersonaResult<Persona>
}

