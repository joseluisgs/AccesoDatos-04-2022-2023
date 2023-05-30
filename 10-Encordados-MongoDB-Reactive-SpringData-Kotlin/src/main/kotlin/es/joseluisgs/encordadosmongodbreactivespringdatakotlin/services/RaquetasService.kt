package es.joseluisgs.encordadosmongodbreactivespringdatakotlin.services

import es.joseluisgs.encordadosmongodbreactivespringdatakotlin.models.Raqueta
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ChangeStreamEvent
import org.springframework.data.mongodb.core.ChangeStreamOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class RaquetasService
@Autowired constructor(
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
) {
    fun watch(): Flow<ChangeStreamEvent<Raqueta>> {
        logger.info("watch()")
        return reactiveMongoTemplate.changeStream(
            "raquetas",
            ChangeStreamOptions.empty(),
            Raqueta::class.java
        ).asFlow()
    }
}