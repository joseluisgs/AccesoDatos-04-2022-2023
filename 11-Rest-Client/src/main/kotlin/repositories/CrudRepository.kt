package repositories

import kotlinx.coroutines.flow.Flow

// Vamos a simular
// https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html
interface CrudRepository<T, ID> {
    suspend fun findAll(page: Int, perPage: Int): Flow<T>
    suspend fun findById(id: ID): T // nullable puede no existir
    suspend fun save(entity: T): T // Inserta
    suspend fun update(entity: T): T // Inserta si no existe, actualiza si existe
    suspend fun delete(entity: T): T // No es obligatorio el boolean
    //suspend fun findAllWithToken(token: String, page: Int, perPage: Int): Flow<T>

}