package eu.ha3.x.sff.connector.vertx.coroutine

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import eu.ha3.x.sff.connector.vertx.*
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
import eu.ha3.x.sff.system.SDocSystem
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2019-01-14
 *
 * @author Ha3
 */
@ExtendWith(VertxExtension::class)
internal class SDocSystemVertxTest {
    private lateinit var vertx: Vertx
    private lateinit var docSystem: SDocSystem

    @BeforeEach
    fun setUp(context: VertxTestContext) {
        docSystem = mock()
        vertx = Vertx.vertx()
        vertx.eventBus().registerDefaultCodec(DJsonObject::class.java, DJsonObjectMessageCodec())
        vertx.deployVerticle(SDocSystemVertx().Verticle(docSystem), context.succeeding {
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
    fun `it should delegate listAll`(context: VertxTestContext) {
        val async = context.checkpoint()
        val expected = DocListResponse(listOf(Doc("basicName", ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC))))
        docSystem.stub {
            onBlocking { listAll() }.doReturn(expected)
        }

        runBlocking {
            // Exercise
            val res = SEventBus(vertx, Jsonify.mapper).ssSend<DocListResponse>(DEvent.SYSTEM_LIST_DOCS.toString(), NoMessage)

            // Verify
            context.verify {
                Assertions.assertThat(res.answer).isEqualTo(expected)
            }

            async.flag()
        }
    }
}
