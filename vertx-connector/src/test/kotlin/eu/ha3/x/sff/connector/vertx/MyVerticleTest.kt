package eu.ha3.x.sff.connector.vertx

import io.vertx.core.Vertx
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(VertxUnitRunner::class)
class MyVerticleTest {
    private var vertx: Vertx? = null

    @Before
    fun setUp(context: TestContext) {
        vertx = Vertx.vertx()
        vertx!!.deployVerticle(MyVerticle::class.java!!.getName(),
                context.asyncAssertSuccess<String>())
    }

    @After
    fun tearDown(context: TestContext) {
        vertx!!.close(context.asyncAssertSuccess())
    }

    @Test
    fun `it should return a greeting prettily`(context: TestContext) {
        val async = context.async()

        vertx!!.createHttpClient().getNow(8080, "localhost", "/greeting"
        ) { response ->
            response.handler { body ->
                context.assertEquals(body.toString(), """{
  "id" : 0,
  "content" : "Hello, World"
}""")
                async.complete()
            }
        }
    }
}