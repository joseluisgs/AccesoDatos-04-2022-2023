import controllers.PersonasController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.Persona.Persona
import models.Persona.PersonaResponseError
import models.Persona.PersonaResponseSuccess
import repositories.personas.PersonasRepositoryImp

fun main(args: Array<String>): Unit = runBlocking {
    println("Hola Results y tipado de datos")

    val controller = PersonasController(PersonasRepositoryImp())

    // creamos el listener de tiempo real para el flow
    val listenerPersonas = launch {
        val res = controller.getAllAsFlow()
            .onStart { println("✔ Iniciando el listener de personas") }
            .distinctUntilChanged()
            //.onEach { println("\uD83D\uDD37 Se han actualizado los datos") }
            .collect { println("\uD83D\uDC49 Personas: $it") }
    }

    delay(1000)
    println("Iniciando el proceso de creación de personas")

    // creamos 10 personas
    println("Creando 10 personas")
    for (i in 1..10) {
        val res = controller.save(Persona(nombre = "Persona $i", edad = i))
        when (res) {
            is PersonaResponseSuccess -> println("Persona creada: ${res.data}")
            is PersonaResponseError -> println("Error: ${res.message}")
        }
    }

    delay(1000)

    // ahora lo pedimos como lista
    println("Obteniendo todas las personas")
    val res = controller.getAll()
    val personas = when (res) {
        is PersonaResponseSuccess -> res.data
        is PersonaResponseError -> {
            println("Error: ${res.message}")
            emptyList()
        }
    }

    personas.forEach { println(it) }

    delay(1000)

    // vamos a actualizar una persona
    println("Actualizando una persona con id: ${personas[0].uuid}")
    val resUpdate = controller.update(personas[0].copy(edad = 100))
    when (resUpdate) {
        is PersonaResponseSuccess -> println("Persona actualizada: ${resUpdate.data}")
        is PersonaResponseError -> println("Error: ${resUpdate.message}")
    }


    delay(1000)

    // vamos a borrar una persona
    println("Borrando una persona con id: ${personas[0].uuid}")
    val resDelete = controller.delete(personas[0])
    when (resDelete) {
        is PersonaResponseSuccess -> println("Persona borrada: ${resDelete.data}")
        is PersonaResponseError -> println("Error: ${resDelete.message}")
    }

    // Ahora por id
    println("Borrando una persona con id: ${personas[1].uuid}")
    val resDeleteById = controller.deleteById(personas[1].uuid)
    when (resDeleteById) {
        is PersonaResponseSuccess -> println("Persona borrada: ${resDeleteById.data}")
        is PersonaResponseError -> println("Error: ${resDeleteById.message}")
    }


    delay(1000)
    // cancelamos el listener
    listenerPersonas.cancel()

}
