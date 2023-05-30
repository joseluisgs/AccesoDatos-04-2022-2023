import controllers.MutuaController
import db.MongoDbManager
import db.getRaquetasInit
import db.getRepresentantesInit
import db.getTenistasInit
import extensions.toLocalMoney
import kotlinx.coroutines.*
import kotlinx.coroutines.reactive.collect
import models.Tenista
import mu.KotlinLogging
import repositories.TenistasRepositoryImpl
import services.TenistasService

private val logger = KotlinLogging.logger {}

fun main(): Unit = runBlocking {
    println("\uD83C\uDFBE Mutua Madrid Open - Gestión de Tenistas \uD83C\uDFBE")

    val limpiar = launch {
        limpiarDatos()
    }
    limpiar.join()

    // Creamos nuestro controlador y le añadimos y le inyectamos las dependencias
    val controller = MutuaController(TenistasRepositoryImpl(), TenistasService())
    val tenistas = mutableListOf<Tenista>()

    // eventos en tiempo real, solo si tienes una replica set local o Mongo Atlas
    // debes mirar como montarte una replica set local en tu ordenador o usar Mongo Atlas
    val listenerTenista = launch {
        println("✔ Escuchando cambios en Tenistas...")
        controller.watchTenistas()
            .collect {
                println("\uD83D\uDC49 Evento: ${it.operationType.value} -> ${it.fullDocument}")
            }
    }

    val init = launch {

        val representantesInit = getRepresentantesInit()
        val raquetasInit = getRaquetasInit()
        // Asignamos a cada raqueta su represnetante
        raquetasInit.forEachIndexed { index, raqueta ->
            raqueta.represetante = representantesInit[index]
        }
        val tenistasInit = getTenistasInit()
        tenistasInit[0].raqueta = raquetasInit[0] // Nadal, Babolat
        tenistasInit[1].raqueta = raquetasInit[2] // Federer, Wilson !! Están ordenadas!!
        tenistasInit[2].raqueta = raquetasInit[1] // Djokovic, Head
        tenistasInit[3].raqueta = raquetasInit[0] // Thiem, Babolat
        tenistasInit[4].raqueta = raquetasInit[0] // Alcaraz, Babolat


        // Insertamos los tenistas
        tenistasInit.forEach { tenista ->
            controller.createTenista(tenista)
        }

        // Obtenemos todos los tenistas y los mostramos
        tenistas.clear()
        controller.getTenistas().collect { tenista ->
            tenistas.add(tenista)
        }
        tenistas.forEach { tenista ->
            println(tenista)
        }
    }
    init.join()

    delay(1000)

    val update = launch {

        // Vamos a buscar a un tenista por su uuid
        val tenista = controller.getTenistaById(tenistas[4].id)
        // Si no es null lo imprimimos, sabemos que no lo es, pero y si no lo encuentra porque el uuid es incorrecto?
        tenista?.let { println(it) }

        // Vamos a cambiarle sus ganancias, que Carlos ha ganado el torneo
        tenista?.let {
            it.ganancias += 1000000.0
            controller.updateTenista(it)
        }

        // vamos a buscarlo otra vez, para ver los cambios
        controller.getTenistaById(tenistas[4].id)?.let { println(it) }

        // Vamos a borrar a Roger Federer, porque se retira
        val roger = controller.getTenistaById(tenistas[1].id)
        roger?.let { if (controller.deleteTenista(it)) println("Roger Federer eliminado") }

        // Sacamos todos los tenistas otra vez
        tenistas.clear()
        controller.getTenistas().collect { tenista ->
            tenistas.add(tenista)
        }
        tenistas.sortedBy { it.ranking }.forEach { tenista ->
            println(tenista)
        }
    }
    update.join()

    // Ademas podemos jugar con los tenistas
    // Tenista que más ha ganado
    println()
    println("Tenista con mas ganancias:  ${tenistas.maxBy { it.ganancias }}")
    // Tenista más novel en el circuito
    println("Tenista más novel: ${tenistas.maxBy { it.añoProfesional }}")
    // Tenista más veterano en el circuito
    println("Tenista más veterano: ${tenistas.minBy { it.añoProfesional }}")
    // Tenista más alto
    println("Tenista más alto: ${tenistas.maxBy { it.altura }}")
    // Agrupamos por nacionalidad
    println("Tenistas por nacionalidad ${tenistas.groupBy { it.pais }}")
    // Agrupamos por mano hábil
    println("Tenistas por mano hábil: ${tenistas.groupBy { it.manoDominante }}")
    // ¿Cuantos tenistas a un o dos manos hay?
    val manos = tenistas.groupBy { it.manoDominante }
    manos.forEach { (mano, tenistas) ->
        println("Tenistas con $mano: ${tenistas.size}")
    }

    // ¿Cuantos tenistas hay por cada raqueta?
    println("Cuantos tenistas hay por cada raqueta")
    val tenistasRaquetas = tenistas.groupBy { it.raqueta } // Así de simple!!!
    tenistasRaquetas.forEach { (raqueta, tenistas) ->
        println("Tenistas con ${raqueta?.marca}: ${tenistas.size}")
    }

    // La raqueta más cara
    println("Raqueta más cara")
    val raqueta = tenistas.mapNotNull { it.raqueta }.maxBy { it.precio }
    println(raqueta)
    // ¿Qué tenista usa la raqueta más cara?
    println("Tenista con la raqueta más cara: ${tenistas.maxBy { it.raqueta?.precio ?: 0.0 }}")
    // Ganancias totales de todos los tenistas
    println("Ganancias totales: ${tenistas.sumOf { it.ganancias }.toLocalMoney()}")
    // Precio medio de las raquetas
    println("Precio medio de las raquetas")
    val precioMedio = tenistas.mapNotNull { it.raqueta }.map { it.precio }.average()
    println(precioMedio.toLocalMoney())

    delay(1000)

    listenerTenista.cancel()

}

suspend fun limpiarDatos() = withContext(Dispatchers.IO) {
    // incosostencia temporal usar stimatedCount y count
    /*if (MongoDbManager.database.getCollection<Tenista>().countDocuments() > 0) {
        MongoDbManager.database.getCollection<Tenista>().drop()
    }*/
    if (MongoDbManager.database.getCollection<Tenista>().estimatedDocumentCount() > 0) {
        logger.debug { "Borrando datos de la base de datos" }
        MongoDbManager.database.getCollection<Tenista>().drop()
    }
}
