package eu.ha3.x.sff.connector.vertx

import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(VertxUnitRunner::class)
class WebVerticleTest {
    private lateinit var vertx: Vertx

    @Before
    fun setUp(context: TestContext) {
        vertx = Vertx.vertx()
        vertx.deployVerticle(WebVerticle::class.java.getName(),
                context.asyncAssertSuccess<String>())
    }

    @After
    fun tearDown(context: TestContext) {
        vertx.close(context.asyncAssertSuccess())
    }

    @Test
    fun `it should return a greeting prettily`(context: TestContext) {
        // Setup
        val async = context.async()
        vertx.eventBus().consumer(VEvent.GREETING.toString()) { msg: Message<Unit> ->
            msg.reply(Unit)
        }

        // Exercise
        vertx.createHttpClient().getNow(8080, "localhost", "/greeting"
        ) { response ->
            response.handler { body ->
                // Verify
                context.assertEquals(body.toString().replace("\r\n", "\n"), """{
  "id" : 0,
  "content" : "Hello, World"
}""")
                async.complete()
            }
        }
    }
}