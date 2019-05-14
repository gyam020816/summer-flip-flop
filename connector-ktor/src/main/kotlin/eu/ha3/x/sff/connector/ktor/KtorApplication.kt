package eu.ha3.x.sff.connector.ktor

import com.fasterxml.jackson.databind.ObjectMapper
import eu.ha3.x.sff.api.SDocStorage
import eu.ha3.x.sff.core.DocCreateRequest
import eu.ha3.x.sff.staticcontent.swagger.ApiSpecification
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resource
import io.ktor.http.content.static
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
import io.ktor.webjars.Webjars
import io.swagger.v3.core.util.Json
import io.swagger.v3.core.util.Yaml
import java.time.ZoneId

/**
 * (Default template)
 * Created on 2019-05-05
 *
 * @author Ha3
 */

fun Application.main(docStorage: SDocStorage, webObjectMapper: ObjectMapper) {
    install(Webjars) {
        zone = ZoneId.of("UTC")
    }
    routing {
        static {
            resource("/swagger.html", "ktor-static/swagger.html")
        }
        install(ContentNegotiation) {
            register(ContentType.Application.Json, JacksonConverter(webObjectMapper))
        }
        install(StatusPages) {
            exception<com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException> { cause ->
                call.respondText("Bad Request", ContentType.Text.Plain, HttpStatusCode.BadRequest)
            }
        }

        val yamlSpec by lazy {
            Yaml.mapper().writeValueAsString(ApiSpecification.newInstance())
        }
        val jsonSpec by lazy {
            Json.mapper().writeValueAsString(ApiSpecification.newInstance())
        }
        get("/swagger.yaml") {
            call.respondText(ContentType.parse("application/x-yaml; charset=UTF-8"), HttpStatusCode.OK) { yamlSpec }
        }
        get("/swagger.json") {
            call.respondText(ContentType.Application.Json, HttpStatusCode.OK) { jsonSpec }
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
