package repositories

import kotlinx.coroutines.flow.Flow
import models.Tenista

// Vamos a simular, ahora es async y reactivo
// https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html
interface CrudRepository<T, ID> {
    fun findAll(): Flow<T> // List<T> es una lista de T
    suspend fun findById(id: ID): T? // nullable puede no existir
    suspend fun save(entity: T): Tenista? // Inserta si no existe, actualiza si existe
    suspend fun delete(entity: T): Boolean // No es obligatorio el boolean
}