package eu.ha3.x.sff.connector.vertx

import io.vertx.core.AsyncResult
import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(VertxUnitRunner::class)
class ConnectorVerticleTest {
    private lateinit var vertx: Vertx

    @Before
    fun setUp(context: TestContext) {
        vertx = Vertx.vertx()
        vertx.deployVerticle(ConnectorVerticle::class.java.getName(),
                context.asyncAssertSuccess<String>())
    }

    @After
    fun tearDown(context: TestContext) {
        vertx.close(context.asyncAssertSuccess())
    }

    @Test
    fun `it should process the greeting`(context: TestContext) {
        val async = context.async()

        // Exercise
        vertx.eventBus().send(VEvent.GREETING.toString(), "") { res: AsyncResult<Message<String>> ->
            async.complete()
        }
    }
}