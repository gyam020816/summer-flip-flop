package eu.ha3.x.sff.connector.kgraphql

import com.fasterxml.jackson.databind.ObjectMapper
import eu.ha3.x.sff.api.SDocStorage
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.jackson.JacksonConverter
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.webjars.Webjars
import java.time.ZoneId

/**
 * (Default template)
 * Created on 2019-05-05
 *
 * @author Ha3
 */
data class GraphQLRequest(val query: String = "", val operationName: String?)

fun Application.main(schema: KGraphqlSchema, webObjectMapper: ObjectMapper) {
    install(Webjars) {
        zone = ZoneId.of("UTC")
    }
    routing {
        install(ContentNegotiation) {
            register(ContentType.Application.Json, JacksonConverter(webObjectMapper))
        }
        post("/graphql") {
            val request = call.receive<GraphQLRequest>()
            val query = request.query

            call.respondText(schema.schema().execute(query), ContentType.Application.Json)
        }
    }
}

object KtorGraphqlApplication {
    fun newEmbedded(docStorage: SDocStorage, webObjectMapper: ObjectMapper): NettyApplicationEngine {
        return embeddedServer(Netty, port = 8080) {
            this.main(KGraphqlSchema(docStorage), webObjectMapper)
        }
    }
}
