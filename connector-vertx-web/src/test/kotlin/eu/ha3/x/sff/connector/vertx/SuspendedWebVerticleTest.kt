package eu.ha3.x.sff.connector.vertx

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import eu.ha3.x.sff.api.SDocStorage
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocCreateRequest
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.json.KObjectMapper
import eu.ha3.x.sff.test.TestSample
import eu.ha3.x.sff.test.testBlocking
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
class SuspendedWebVerticleTest {
    private lateinit var vertx: Vertx
    private val webObjectMapper = KObjectMapper.newInstance()
    private var mockDocStorage : SDocStorage = mock()

    @BeforeEach
    fun setUp(context: VertxTestContext) = testBlocking {
        vertx = Vertx.vertx()

        vertx.delegate.eventBus().registerDefaultCodec(DJsonObject::class.java, DJsonObjectMessageCodec())
        vertx.delegate.deployVerticle(SuspendedWebVerticle(mockDocStorage, webObjectMapper), context.succeeding {
            context.completeNow()
        })
    }

    @AfterEach
    fun tearDown(context: VertxTestContext) = testBlocking {
        vertx.close(context.succeeding {
            context.completeNow()
        })
    }

    @Test
    fun `it should return the docs`(context: VertxTestContext) = testBlocking {
        val async = context.checkpoint()
        val expected = listOf(Doc("someDoc", TestSample.zonedDateTime))
        mockDocStorage.stub {
            onBlocking { listAll() }.doReturn(DocListResponse(expected))
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
    fun `it should append the docs`(context: VertxTestContext) = testBlocking {
        val async = context.checkpoint()
        val request = DocCreateRequest("someDoc")
        val expected = Doc("someDoc", TestSample.zonedDateTime)
        mockDocStorage.stub {
            onBlocking { appendToDocs(request) }.doReturn(expected)
        }

        // Exercise
        WebClient.create(vertx)
                .post(8080, "localhost", "/docs")
                .sendJson(DMapper(CodecObjectMapper.mapper).asJsonObject(request).inner) { response ->
                    // Verify
                    context.verify {
                        val result = response.result()

                        assertThatJson(result.bodyAsString()).isEqualTo(DMapper(CodecObjectMapper.mapper).asJsonObject(expected).inner.encodePrettily())
                        assertThat(result.statusCode()).isEqualTo(201)
                        async.flag()
                    }
                }
    }

    @Test
    fun `it should error with bad request`(context: VertxTestContext) = testBlocking {
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