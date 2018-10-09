package eu.ha3.x.sff.connector.vertx

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.test.TestSample
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class WebVerticleTest {
    private lateinit var vertx: Vertx

    @BeforeEach
    fun setUp(context: VertxTestContext) {
        vertx = Vertx.vertx()

        vertx.deployVerticle(WebVerticle::class.java.getName(), context.succeeding {
            context.completeNow()
        })
    }

    @AfterEach
    fun tearDown(context: VertxTestContext) {
        vertx.close(context.succeeding {
            context.completeNow()
        })
    }

    /*
    @Test
    fun `it should return the docs`(context: io.vertx.junit5.VertxTestContext) {
        // Setup
        val async = context.async()
        vertx.eventBus().consumer<JsonObject>(VEvent.SEARCH.toString()) { msg ->
            val query = msg.body().mapTo(SearchQuery::class.java)
            val result = SearchResult(query.query)
            msg.reply(JsonObject.mapFrom(result))
        }
        val QUERY = "TheQuery"

        // Exercise
        vertx.createHttpClient().getNow(8080, "localhost", "/search?query=$QUERY"
        ) { response ->
            response.handler { body ->
                // Verify
                context.assertEquals(body.toString().replace("\r\n", "\n"), """[ {
  "result" : "$QUERY"
} ]""")
                async.complete()
            }
        }
    }
    */

    @Test
    fun `it should return the docs`(context: VertxTestContext) {
        val async = context.checkpoint()
        val expected = listOf(Doc("someDoc", TestSample.zonedDateTime))
        vertx.eventBus().consumer<JsonObject>(DEvent.LIST_DOCS.address()) { msg ->
            msg.reply(DocListResponse(expected).jsonify())
        }

        // Exercise
        vertx.createHttpClient().getNow(8080, "localhost", "/docs"
        ) { response ->
            response.handler { body ->
                // Verify
                context.verify {
                    assertThat(body.toString().replace("\r\n", "\n")).isEqualTo("""[ {
  "name" : "someDoc",
  "createdAt" : "${TestSample.zonedDateTimeSerialized}"
} ]""")
                    async.flag()
                }
            }
        }
    }
}