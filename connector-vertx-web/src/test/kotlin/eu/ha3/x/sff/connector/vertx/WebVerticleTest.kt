package eu.ha3.x.sff.connector.vertx

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import eu.ha3.x.sff.core.Doc
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.ZonedDateTime

@ExtendWith(VertxExtension::class)
class WebVerticleTest {
    private lateinit var vertx: Vertx

    @BeforeEach
    fun setUp(context: VertxTestContext) {
        vertx = Vertx.vertx()
        listOf(Json.mapper, Json.prettyMapper).forEach { mapper ->
            mapper.registerModule(JavaTimeModule())
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        }

        vertx.deployVerticle(WebVerticle::class.java.getName(),
                context.succeeding())
        vertx.eventBus().registerDefaultCodec(NoMessage::class.java, NoMessageCodec())
        IResponse.classes.forEach { klass ->
            vertx.eventBus().registerDefaultCodec(klass, ResponseCodec(klass))
        }

        context.completeNow()
    }

    @AfterEach
    fun tearDown(context: VertxTestContext) {
        vertx.close(context.succeeding())

        context.completeNow()
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

        // Setup
        vertx.eventBus().consumer<NoMessage>(DEvent.LIST_DOCS.address()) { msg ->
            msg.reply(DocListResponse(listOf(Doc("someDoc", ZonedDateTime.parse("2018-10-07T02:34:43.308+02:00")))))
        }

        // Exercise
        vertx.createHttpClient().getNow(8080, "localhost", "/docs"
        ) { response ->
            response.handler { body ->
                // Verify
                context.verify {
                    assertThat(body.toString().replace("\r\n", "\n")).isEqualTo("""[ {
  "name" : "someDoc",
  "createdAt" : "2018-10-07T02:34:43.308+02:00"
} ]""")
                    async.flag()
                }
            }
        }
    }
}