package models.Persona


sealed class PersonaResponse<Persona>

class PersonaResponseSuccess<T : Any>(code: Int, val data: T) : PersonaResponse<T>()

// Responses
//class PersonaResponseAsList(val code: Int, val data: List<Persona>) : PersonaResponse<List<Persona>>()

//class PersonaResponseAsFlow(val code: Int, val data: Flow<Persona>) : PersonaResponse<Flow<Persona>>()

//class PersonaResponseAsEntity(val code: Int, val data: Persona?) : PersonaResponse<Persona>()

// Errors y Excepciones
class PersonaResponseError(val code: Int, val message: String) : PersonaResponse<Nothing>()
// class PersonaResponseException(val code: Int, val message: String) : PersonaResponse<Nothing>()

