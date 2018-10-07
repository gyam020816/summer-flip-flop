package eu.ha3.x.sff.connector.vertx

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import eu.ha3.x.sff.core.Doc
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.ZonedDateTime

@RunWith(VertxUnitRunner::class)
class WebVerticleTest {
    private lateinit var vertx: Vertx

    @Before
    fun setUp(context: TestContext) {
        vertx = Vertx.vertx()
        listOf(Json.mapper, Json.prettyMapper).forEach { mapper ->
            mapper.registerModule(JavaTimeModule())
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        }

        vertx.deployVerticle(WebVerticle::class.java.getName(),
                context.asyncAssertSuccess<String>())
        vertx.eventBus().registerDefaultCodec(NoMessage::class.java, NoMessageCodec())
        IResponse.classes.forEach { klass ->
            vertx.eventBus().registerDefaultCodec(klass, ResponseCodec(klass))
        }
    }

    @After
    fun tearDown(context: TestContext) {
        vertx.close(context.asyncAssertSuccess())
    }

    /*
    @Test
    fun `it should return the docs`(context: TestContext) {
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
    fun `it should return the docs`(context: TestContext) {
        // Setup
        val async = context.async()
        vertx.eventBus().consumer<NoMessage>(DEvent.LIST_DOCS.address()) { msg ->
            msg.reply(DocListResponse(listOf(Doc("someDoc", ZonedDateTime.parse("2018-10-07T02:34:43.308+02:00")))))
        }
        val QUERY = "TheQuery"

        // Exercise
        vertx.createHttpClient().getNow(8080, "localhost", "/docs"
        ) { response ->
            response.handler { body ->
                // Verify
                context.assertEquals(body.toString().replace("\r\n", "\n"), """[ {
  "name" : "someDoc",
  "createdAt" : "2018-10-07T02:34:43.308+02:00"
} ]""")
                async.complete()
            }
        }
    }
}