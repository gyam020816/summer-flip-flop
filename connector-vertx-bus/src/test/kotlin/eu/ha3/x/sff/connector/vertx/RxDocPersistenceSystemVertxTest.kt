package eu.ha3.x.sff.connector.vertx

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
import eu.ha3.x.sff.system.RxDocPersistenceSystem
import io.reactivex.Single
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.rxjava.core.Vertx
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2018-10-07
 *
 * @author Ha3
 */
@ExtendWith(VertxExtension::class)
internal class RxDocPersistenceSystemVertxTest {
    private lateinit var vertx: Vertx
    private lateinit var docSystem: RxDocPersistenceSystem

    @BeforeEach
    fun setUp(context: VertxTestContext) {
        docSystem = mock()
        vertx = Vertx.vertx()
        vertx.delegate.eventBus().registerDefaultCodec(DJsonObject::class.java, DJsonObjectMessageCodec())
        vertx.delegate.deployVerticle(RxDocSystemVertx().Verticle(docSystem), context.succeeding {
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
            on { listAll() }.doReturn(Single.just(expected))
        }

        // Exercise
        DEventBus(vertx.eventBus(), CodecObjectMapper.mapper).dsSend<DocListResponse>(DEvent.SYSTEM_LIST_DOCS.toString(), NoMessage).subscribe({ res ->
            context.verify {
                assertThat(res.answer).isEqualTo(expected)
            }

            async.flag()
        }, context::failNow)
    }
}