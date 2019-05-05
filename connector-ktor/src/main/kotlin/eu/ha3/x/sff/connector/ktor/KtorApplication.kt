package eu.ha3.x.sff.connector.ktor

import com.fasterxml.jackson.databind.ObjectMapper
import eu.ha3.x.sff.api.SDocStorage
import eu.ha3.x.sff.core.DocCreateRequest
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveText
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

/**
 * (Default template)
 * Created on 2019-05-05
 *
 * @author Ha3
 */

fun Application.main(docStorage: SDocStorage, webObjectMapper: ObjectMapper) {
    routing {
        post("/docs") {
            try {
                val docCreateRequest = webObjectMapper.readValue(call.receiveText(), DocCreateRequest::class.java)
                val result = docStorage.appendToDocs(docCreateRequest)
                call.respondText(webObjectMapper.writeValueAsString(result), ContentType.Application.Json, HttpStatusCode.Created)

            } catch (e: com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException) {
                call.respondText("Bad Request", ContentType.Text.Plain, HttpStatusCode.BadRequest)
            }
        }
        get("/docs") {
            val result = docStorage.listAll()
            call.respondText(webObjectMapper.writeValueAsString(result.data), ContentType.Application.Json, HttpStatusCode.OK)
        }
    }
}

object KtorApplication {
    fun newEmbedded(docStorage: SDocStorage, webObjectMapper: ObjectMapper) {
        embeddedServer(Netty, port = 8080) {
            this.main(docStorage, webObjectMapper)
        }
    }
}
