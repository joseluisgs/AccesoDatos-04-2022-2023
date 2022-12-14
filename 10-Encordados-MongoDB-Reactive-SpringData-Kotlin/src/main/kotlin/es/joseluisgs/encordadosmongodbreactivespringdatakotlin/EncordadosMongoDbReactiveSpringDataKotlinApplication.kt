package es.joseluisgs.encordadosmongodbreactivespringdatakotlin

import es.joseluisgs.encordadosmongodbreactivespringdatakotlin.controller.MutuaController
import es.joseluisgs.encordadosmongodbreactivespringdatakotlin.db.getRaquetasInit
import es.joseluisgs.encordadosmongodbreactivespringdatakotlin.db.getRepresentantesInit
import es.joseluisgs.encordadosmongodbreactivespringdatakotlin.db.getTenistasInit
import es.joseluisgs.encordadosmongodbreactivespringdatakotlin.extensions.toLocalMoney
import es.joseluisgs.encordadosmongodbreactivespringdatakotlin.models.Raqueta
import es.joseluisgs.encordadosmongodbreactivespringdatakotlin.models.Tenista
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import kotlin.system.exitProcess

// Para hacer un replica!!!
// https://www.vinsguru.com/mongodb-change-streams-reactive-spring-data/

// O usa Mongo Atlas para los watch!!!

@SpringBootApplication
class EncordadosMongoDbReactiveSpringDataKotlinApplication
@Autowired constructor(
    private val controller: MutuaController
) : CommandLineRunner {

    override fun run(vararg args: String?): Unit = runBlocking {
        println("\uD83C\uDFBE Mutua Madrid Open - Gestión de Tenistas \uD83C\uDFBE")

        val tenistas = mutableListOf<Tenista>()
        val raquetas = mutableListOf<Raqueta>()

        val tenistasInit = getTenistasInit()
        val raquetasInit = getRaquetasInit()
        val represetantesInit = getRepresentantesInit()

        // Limpiamos los datos
        // Lo meto en corrutinas solo para mostrar los ejemplos
        val clear = launch {
            controller.raquetasDeleteAll()
            controller.tenistasDeleteAll()
        }
        clear.join()

        // Estos listeners son para ver los cambios en la base de datos
        // solo funcionan con Mongo Atlas o Mongo Replica en tu ordenador

        val tenistasListener = launch {
            controller.watchTenistas()
                .onStart { println("✔ Escuchando cambios en Tenistas...") }
                .collect { println("\uD83D\uDC49 Evento: ${it.operationType?.value} -> Tenista: ${it.body}") }
        }

        val raquetasListener = launch {
            controller.watchRaquetas()
                .onStart { println("✔ Escuchando cambios en Raquetas...") }
                .collect { println("\uD83C\uDFBE Evento: ${it.operationType?.value} -> Raqueta: ${it.body}") }
        }


        // Creamos las raquetas
        val init = launch {
            // Asignamos los representantes a las raquetas
            raquetasInit.forEachIndexed { index, raqueta ->
                raqueta.representante = represetantesInit[index]
            }

            raquetasInit.forEach { controller.createRaqueta(it) }
            // Obtenemos las raquetas

            // Asignamos a los tenistas las raquetas
            tenistasInit[0].raqueta = raquetasInit[0]
            tenistasInit[1].raqueta = raquetasInit[1]
            tenistasInit[2].raqueta = raquetasInit[2]
            tenistasInit[3].raqueta = raquetasInit[0]
            tenistasInit[4].raqueta = raquetasInit[0]

            // Insertamos los tenistas
            tenistasInit.forEach { controller.createTenista(it) }
            // Obtenemos los tenistas

            tenistas.clear()
            raquetas.clear()
            controller.getTenistas().collect { tenistas.add(it) }
            controller.getRaquetas().collect { raquetas.add(it) }

            println("Raquetas")
            raquetas.forEach { println(it) }

            println("Tenistas")
            tenistas.forEach { println(it) }
        }

        init.join()

        val auto = launch {
            // Vamos a buscar a un tenista por su uuid
            var tenista = controller.getTenistaById(tenistas[4].id)
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
            controller.getTenistas().collect {
                // println("Tenista: $it")
                tenistas.add(it)
            }
            println("Tenistas ordenados por ranking")
            tenistas.sortedBy { it.ranking }.forEach { println(it) }

        }
        auto.join()

        // Ademas podemos jugar con los tenistas
        // Tenista que más ha ganado
        println()

        println("Tenista con mas ganancias:  ${tenistas.maxByOrNull { it.ganancias }}")
        // Tenista más novel en el circuito
        println("Tenista más novel: ${tenistas.maxByOrNull { it.añoProfesional }}")
        // Tenista más veterano en el circuito
        println("Tenista más veterano: ${tenistas.minByOrNull { it.añoProfesional }}")
        // Tenista más alto
        println("Tenista más alto: ${tenistas.maxByOrNull { it.altura }}")
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
        val tenistasRaquetas = tenistas.groupBy { it.raqueta } // Así de simple!!!
        tenistasRaquetas.forEach { (raqueta, tenistas) ->
            println("Tenistas con ${raqueta?.marca}: ${tenistas.size}")
        }
        // La raqueta más cara
        println("Raqueta más cara: ${raquetas.maxByOrNull { it.precio }}")
        // ¿Qué tenista usa la raqueta más cara?
        println("Tenista con la raqueta más cara: ${tenistas.maxByOrNull { it.raqueta?.precio ?: 0.0 }}")
        // Ganancias totales de todos los tenistas
        println("Ganancias totales: ${tenistas.sumOf { it.ganancias }.toLocalMoney()}")
        // Precio medio de las raquetas
        println("Precio medio de las raquetas: ${raquetas.map { it.precio }.average().toLocalMoney()}")

        tenistasListener.cancel()
        raquetasListener.cancel()

        exitProcess(0)

    }

}

fun main(args: Array<String>) {
    runApplication<EncordadosMongoDbReactiveSpringDataKotlinApplication>(*args)
}
