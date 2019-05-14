package eu.ha3.x.sff.connector.ktor

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import eu.ha3.x.sff.api.SDocStorage
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocCreateRequest
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.json.KObjectMapper
import eu.ha3.x.sff.test.TestSample
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.testing.*
import net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * (Default template)
 * Created on 2019-05-05
 *
 * @author Ha3
 */
class KtorApplicationKtTest {
    private val mockDocStorage: SDocStorage = mock()
    private val webObjectMapper = KObjectMapper.newInstance()

    @Test
    fun `it should serve swagger html`() = withKtor {
        // Exercise
        handleRequest(HttpMethod.Get, "/swagger.html") {
        }.apply {
            // Verify
            assertThat(response.status()).isNotNull()
                    .extracting { it!!.value }.isEqualTo(200)
            assertThat(response.contentType()).isEqualTo(ContentType.parse("text/html; charset=UTF-8"))
            assertThat(response.content)
                    .startsWith("<!DOCTYPE html>")
                    .contains("/swagger.json")
        }
    }

    @Test
    fun `it should serve swagger json`() = withKtor {
        // Exercise
        handleRequest(HttpMethod.Get, "/swagger.json") {
        }.apply {
            // Verify
            assertThat(response.status()).isNotNull()
                    .extracting { it!!.value }.isEqualTo(200)
            assertThat(response.contentType()).isEqualTo(ContentType.parse("application/json; charset=UTF-8"))
            assertThat(response.content)
                    .startsWith("{")
                    .contains(""""title":"Summer Flip Flop Exploratory API"""")
                    .endsWith("}")
        }
    }

    @Test
    fun `it should serve swagger yaml`() = withKtor {
        // Exercise
        handleRequest(HttpMethod.Get, "/swagger.yaml") {
        }.apply {
            // Verify
            assertThat(response.status()).isNotNull()
                    .extracting { it!!.value }.isEqualTo(200)
            assertThat(response.contentType()).isEqualTo(ContentType.parse("application/x-yaml; charset=UTF-8"))
            assertThat(response.content)
                    .startsWith("openapi: 3.0.1")
                    .contains("  title: Summer Flip Flop Exploratory API")
        }
    }

    @Test
    fun `it should accept a doc`() = withKtor {
        mockDocStorage.stub {
            onBlocking { appendToDocs(DocCreateRequest("someDoc")) }.doReturn(Doc("someDoc", TestSample.zonedDateTime))
        }

        // Exercise
        handleRequest(HttpMethod.Post, "/docs") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("""{"name": "someDoc"}""")

        }.apply {
            // Verify
            assertThat(response.status()).isNotNull()
                    .extracting { it!!.value }.isEqualTo(201)
            assertThatJson(response.content).isEqualTo("""{"name": "someDoc", "createdAt": "${TestSample.zonedDateTimeSerialized}"}""")
        }
    }

    @Test
    fun `it should return the docs`() = withKtor {
        val expected = listOf(Doc("someDoc", TestSample.zonedDateTime))
        mockDocStorage.stub {
            onBlocking { listAll() }.doReturn(DocListResponse(expected))
        }

        // Exercise
        handleRequest(HttpMethod.Get, "/docs") {
        }.apply {
            // Verify
            assertThat(response.status()).isNotNull()
                    .extracting { it!!.value }.isEqualTo(200)
            assertThatJson(response.content).isEqualTo("""[ {
  "name" : "someDoc",
  "createdAt" : "${TestSample.zonedDateTimeSerialized}"
} ]""")
        }
    }

    @Test
    fun `it should error with bad request`() = withKtor {
        // Exercise
        handleRequest(HttpMethod.Post, "/docs") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("{}")

        }.apply {
            // Verify
            assertThat(response.status()).isNotNull()
                    .extracting { it!!.value }.isEqualTo(400)
            assertThat(response.content).isEqualTo("Bad Request")
        }
    }

    fun withKtor(contextFn: TestApplicationEngine.() -> Unit) = withTestApplication({
        main(mockDocStorage, webObjectMapper)
    }) {
        contextFn(this)
        Unit
    }
}