package eu.ha3.x.sff.connector.ktor

import com.fasterxml.jackson.databind.ObjectMapper
import eu.ha3.x.sff.api.SDocStorage
import eu.ha3.x.sff.core.DocCreateRequest
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.JacksonConverter
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine

/**
 * (Default template)
 * Created on 2019-05-05
 *
 * @author Ha3
 */

fun Application.main(docStorage: SDocStorage, webObjectMapper: ObjectMapper) {
    routing {
        install(ContentNegotiation) {
            register(ContentType.Application.Json, JacksonConverter(webObjectMapper))
        }
        install(StatusPages) {
            exception<com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException> { cause ->
                call.respondText("Bad Request", ContentType.Text.Plain, HttpStatusCode.BadRequest)
            }
        }

        post("/docs") {
            val docCreateRequest = call.receive<DocCreateRequest>()
            val result = docStorage.appendToDocs(docCreateRequest)
            call.respond(HttpStatusCode.Created, result)
        }
        get("/docs") {
            val result = docStorage.listAll().data
            call.respond(HttpStatusCode.OK, result)
        }
    }
}

object KtorApplication {
    fun newEmbedded(docStorage: SDocStorage, webObjectMapper: ObjectMapper): NettyApplicationEngine {
        return embeddedServer(Netty, port = 8080) {
            this.main(docStorage, webObjectMapper)
        }
    }
}
