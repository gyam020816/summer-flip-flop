package eu.ha3.x.sff.connector.kgraphql

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import eu.ha3.x.sff.api.SDocStorage
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.json.KObjectMapper
import eu.ha3.x.sff.test.TestSample
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * (Default template)
 * Created on 2019-05-05
 *
 * @author Ha3
 */
class KtorGraphqlApplicationKtTest {
    private val mockDocStorage: SDocStorage = mock()
    private val webObjectMapper = KObjectMapper.newInstance()

    @Test
    fun `it should accept graphql queries`() = withKtor {
        val expected = listOf(Doc("someDoc", TestSample.zonedDateTime))
        mockDocStorage.stub {
            onBlocking { listAll() }.doReturn(DocListResponse(expected))
        }

        // Exercise
        handleRequest(HttpMethod.Post, "/graphql") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("""{"query": "{\n docs {\n name\n createdAt\n }\n }"}""")
        }.apply {
            // Verify
            assertThat(response.status()).isNotNull()
                    .extracting { it!!.value }.isEqualTo(200)
            assertThatJson(response.content).isEqualTo("""{
  "data" : {
    "docs" : [ {
      "name" : "someDoc",
      "createdAt" : "${TestSample.zonedDateTimeSerialized}"
    } ]
  }
}""")
        }
    }

    fun withKtor(contextFn: TestApplicationEngine.() -> Unit) = withTestApplication({
        main(KGraphqlSchema(mockDocStorage), webObjectMapper)
    }) {
        contextFn(this)
        Unit
    }
}