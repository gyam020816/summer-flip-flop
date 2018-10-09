package eu.ha3.x.sff.connector.vertx

import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.test.TestSample
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.rxjava.core.Vertx
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * (Default template)
 * Created on 2018-10-07
 *
 * @author Ha3
 */
@ExtendWith(VertxExtension::class)
internal class EmitterToDocSystemVerticleTest {
    private lateinit var vertx: Vertx
    private lateinit var SUT: EmitterToDocSystemVerticle

    @BeforeEach
    fun setUp(context: VertxTestContext) {
        vertx = Vertx.vertx()
        vertx.delegate.eventBus().registerDefaultCodec(DJsonObject::class.java, DJsonObjectMessageCodec())

        SUT = EmitterToDocSystemVerticle()
        vertx.delegate.deployVerticle(SUT, context.succeeding { context.completeNow() })
    }

    @AfterEach
    fun tearDown(context: VertxTestContext) {
        vertx.close(context.succeeding { context.completeNow() })
    }

    @Test
    fun `it should emit an event to the bus`(context: VertxTestContext) {
        val async = context.checkpoint()
        val expected = listOf(Doc("someName", TestSample.zonedDateTime))
        vertx.eventBus().consumer<DJsonObject>(DEvent.SYSTEM_LIST_DOCS.address()) { msg ->
            msg.reply(SystemDocListResponse(expected).jsonify())
        }

        SUT.listAll().subscribe({ success ->
            context.verify {
                assertThat(success).isEqualTo(expected)
                async.flag()
            }

        }, context::failNow)

    }
}