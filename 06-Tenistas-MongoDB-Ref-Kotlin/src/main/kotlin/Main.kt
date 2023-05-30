import controllers.MutuaController
import db.MongoDbManager
import db.getRaquetasInit
import db.getRepresentantesInit
import db.getTenistasInit
import extensions.toLocalMoney
import repositories.raquetas.RaquetasRepositoryImpl
import repositories.tenistas.TenistasRepositoryImpl


fun main() {
    println("\uD83C\uDFBE Mutua Madrid Open - Gestión de Tenistas \uD83C\uDFBE")
    limpiarDatos()

    // Creamos nuestro controlador y le añadimos y le inyectamos las dependencias
    val controller = MutuaController(TenistasRepositoryImpl(), RaquetasRepositoryImpl())

    val representantesInit = getRepresentantesInit()
    val raquetasInit = getRaquetasInit()
    // Asignamos a cada raqueta su represnetante
    raquetasInit.forEachIndexed { index, raqueta ->
        raqueta.represetante = representantesInit[index]
    }
    val tenistasInit = getTenistasInit()
    tenistasInit[0].raqueta_id = raquetasInit[0].id // Nadal, Babolat
    tenistasInit[1].raqueta_id = raquetasInit[2].id // Federer, Wilson !! Están ordenadas!!
    tenistasInit[2].raqueta_id = raquetasInit[1].id // Djokovic, Head
    tenistasInit[3].raqueta_id = raquetasInit[0].id // Thiem, Babolat
    tenistasInit[4].raqueta_id = raquetasInit[0].id // Alcaraz, Babolat

    // Insertamos las raquetas
    raquetasInit.forEach { raqueta ->
        controller.createRaqueta(raqueta)
    }

    // Insertamos los tenistas
    tenistasInit.forEach { tenista ->
        controller.createTenista(tenista)
    }

    println("Obteniendo todas las raquetas")
    controller.getRaquetas().forEach { raqueta ->
        println(raqueta)
    }

    // Obtenemos todos los tenistas y los mostramos
    println("Obteniendo todas los tenistas")
    var tenistas = controller.getTenistas()
    tenistas.forEach { tenista ->
        println(tenista)
    }

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
    controller.getTenistaById(tenistasInit[4].id)?.let { println(it) }

    // Vamos a borrar a Roger Federer, porque se retira
    val roger = controller.getTenistaById(tenistas[1].id)
    roger?.let { if (controller.deleteTenista(it)) println("Roger Federer eliminado") }

    // Sacamos todos los tenistas otra vez
    tenistas = controller.getTenistas().sortedBy { it.ranking }
    tenistas.forEach { tenista ->
        println(tenista)
    }

    // Obtenemos todos los tenistas con sus raquetas
    println("Tenistas con raqueta")
    val tenistasDto = controller.getTenistasWithRaqueta()
    tenistasDto.forEach { tenistaDto ->
        println(tenistaDto)
    }

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
    val tenistasRaquetas = tenistasDto.groupBy { it.raqueta } // Así de simple!!!
    tenistasRaquetas.forEach { (raqueta, tenistas) ->
        println("Tenistas con ${raqueta?.marca}: ${tenistas.size}")
    }

    // La raqueta más cara
    println("Raqueta más cara")
    val raqueta = tenistasDto.mapNotNull { it.raqueta }.maxBy { it.precio }
    println(raqueta)
    // ¿Qué tenista usa la raqueta más cara?
    println("Tenista con la raqueta más cara: ${tenistasDto.maxBy { it.raqueta?.precio ?: 0.0 }}")
    // Ganancias totales de todos los tenistas
    println("Ganancias totales: ${tenistas.sumOf { it.ganancias }.toLocalMoney()}")
    // Precio medio de las raquetas
    println("Precio medio de las raquetas")
    val precioMedio = tenistasDto.mapNotNull { it.raqueta }.map { it.precio }.average()
    println(precioMedio.toLocalMoney())


}

fun limpiarDatos() {
    MongoDbManager.database.drop()
    /*if (MongoDbManager.database.getCollection<Tenista>().countDocuments() > 0) {
        MongoDbManager.database.getCollection<Tenista>().drop()
    }
    if (MongoDbManager.database.getCollection<Raqueta>().countDocuments() > 0) {
        MongoDbManager.database.getCollection<Raqueta>().drop()
    }*/

}
