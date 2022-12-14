package es.joseluisgs.encordadosmongodbreactivespringdatakotlin.services

import es.joseluisgs.encordadosmongodbreactivespringdatakotlin.models.Tenista
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ChangeStreamEvent
import org.springframework.data.mongodb.core.ChangeStreamOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Service


// https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/
// https://github.com/spring-projects/spring-data-examples/tree/main/mongodb/change-streams

private val logger = KotlinLogging.logger {}

@Service
class TenistasService
@Autowired constructor(
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
) {
    fun watch(): Flow<ChangeStreamEvent<Tenista>> {
        logger.info("watch()")
        return reactiveMongoTemplate.changeStream(
            "tenistas",
            ChangeStreamOptions.empty(),
            Tenista::class.java
        ).asFlow()
    }
}