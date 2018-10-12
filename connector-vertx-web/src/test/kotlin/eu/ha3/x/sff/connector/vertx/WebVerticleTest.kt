package eu.ha3.x.sff.connector.vertx

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.stub
import eu.ha3.x.sff.api.IDocStorage
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocCreateRequest
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.test.TestSample
import io.reactivex.Single
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.json.JsonObject
import io.vertx.rxjava.core.Vertx
import io.vertx.rxjava.ext.web.client.WebClient
import net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class WebVerticleTest {
    private lateinit var vertx: Vertx
    private lateinit var mockDocStorage : IDocStorage

    @BeforeEach
    fun setUp(context: VertxTestContext) {
        vertx = Vertx.vertx()

        vertx.delegate.eventBus().registerDefaultCodec(DJsonObject::class.java, DJsonObjectMessageCodec())
        mockDocStorage = mock<IDocStorage>()
        vertx.delegate.deployVerticle(WebVerticle(mockDocStorage), context.succeeding {
            context.completeNow()
        })
    }

    @AfterEach
    fun tearDown(context: VertxTestContext) {
        vertx.close(context.succeeding {
            context.completeNow()
        })
    }

    @Test
    fun `it should return the docs`(context: VertxTestContext) {
        val async = context.checkpoint()
        val expected = listOf(Doc("someDoc", TestSample.zonedDateTime))
        mockDocStorage.stub {
            on { listAll() }.doReturn(Single.just(DocListResponse(expected)))
        }

        // Exercise
        vertx.createHttpClient().getNow(8080, "localhost", "/docs"
        ) { response ->
            response.handler { body ->
                // Verify
                context.verify {
                    assertThatJson(body.toString()).isEqualTo("""[ {
  "name" : "someDoc",
  "createdAt" : "${TestSample.zonedDateTimeSerialized}"
} ]""")
                    async.flag()
                }
            }
        }
    }

    @Test
    fun `it should append the docs`(context: VertxTestContext) {
        val async = context.checkpoint()
        val request = DocCreateRequest("someDoc")
        val expected = Doc("someDoc", TestSample.zonedDateTime)
        mockDocStorage.stub {
            on { appendToDocs(request) }.doReturn(Single.just(expected))
        }

        // Exercise
        WebClient.create(vertx)
                .post(8080, "localhost", "/docs")
                .sendJson(request.jsonify().inner) { response ->
                    // Verify
                    context.verify {
                        val result = response.result()

                        assertThatJson(result.bodyAsString()).isEqualTo(expected.jsonify().inner.encodePrettily())
                        assertThat(result.statusCode()).isEqualTo(201)
                        async.flag()
                    }
                }
    }

    @Test
    fun `it should error with bad request`(context: VertxTestContext) {
        val async = context.checkpoint()

        // Exercise
        WebClient.create(vertx)
                .post(8080, "localhost", "/docs")
                .sendJson(JsonObject()) { response ->
                    // Verify
                    context.verify {
                        val result = response.result()

                        assertThat(result.bodyAsString()).isEqualTo("Bad Request") // FIXME: Dubious message
                        assertThat(result.statusCode()).isEqualTo(400)
                        async.flag()
                    }
                }
    }
}