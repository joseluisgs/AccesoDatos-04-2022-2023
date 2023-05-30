package dto

import extensions.toLocalDate
import extensions.toLocalMoney
import extensions.toLocalNumber
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import models.Raqueta
import models.Tenista
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import java.time.LocalDate

@Serializable
data class TenistaDto(
    @BsonId @Contextual
    var id: String? = null,
    var nombre: String,
    var ranking: Int,
    @Contextual
    var fechaNacimiento: LocalDate,
    var añoProfesional: Int,
    var altura: Int,
    var peso: Int,
    var ganancias: Double,
    var manoDominante: Tenista.ManoDominante,
    var tipoReves: Tenista.TipoReves,
    var puntos: Int,
    var pais: String,
    var raqueta: Raqueta? = null
) {

    override fun toString(): String {
        return "TenistaDto(uuid=$id, nombre='$nombre', ranking=$ranking, " +
                "fechaNacimiento=${fechaNacimiento.toLocalDate()}, " +
                "añoProfesional=$añoProfesional, " +
                "altura=${(altura.toDouble() / 100).toLocalNumber()} cm, " +
                "peso=$peso Kg, " +
                "ganancias=${ganancias.toLocalMoney()}, " +
                "manoDominante=${manoDominante.mano}, " +
                "tipoReves=${tipoReves.tipo}, " +
                "puntos=$puntos, pais=$pais" +
                ", raqueta=${raqueta})"
    }
}


private val json: Json = Json { ignoreUnknownKeys = true }

fun Document.toTenistaDto(): TenistaDto {
    val raqueta = this.get("mi_raqueta", List::class.java).firstOrNull()?.let { raqueta ->
        raqueta as Document
        // Más rapido con la serialzación que con como lo dejo comentado!!!
        json.decodeFromString<Raqueta>(raqueta.toJson())

        /*  println("-->" + json.decodeFromString<Raqueta>(raqueta.toJson()))

          // logger.debug { "Document Raqueta: $raqueta" }
          val representante = raqueta.get("represetante", Document::class.java)
          // logger.debug { "Representante Document: $representante" }
          Raqueta(
              raqueta.getString("_id"),
              raqueta.getString("marca"),
              raqueta.getDouble("precio"),
              representante?.let {
                  Representante(
                      representante.getString("_id"),
                      representante.getString("nombre"),
                      representante.getString("email")
                  )
              }
          )
          */
    }
    return TenistaDto(
        this.getString("_id"),
        this.getString("nombre"),
        this.getInteger("ranking"),
        this.getDate("fechaNacimiento").toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
        this.getInteger("añoProfesional"),
        this.getInteger("altura"),
        this.getInteger("peso"),
        this.getDouble("ganancias"),
        Tenista.ManoDominante.valueOf(this.getString("manoDominante")),
        Tenista.TipoReves.valueOf(this.getString("tipoReves")),
        this.getInteger("puntos"),
        this.getString("pais"),
        raqueta
    )
}
