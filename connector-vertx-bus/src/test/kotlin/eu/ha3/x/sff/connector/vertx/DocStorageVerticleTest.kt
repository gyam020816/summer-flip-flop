package eu.ha3.x.sff.connector.vertx

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.stub
import eu.ha3.x.sff.api.IDocStorage
import eu.ha3.x.sff.core.Doc
import io.reactivex.Single
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.rxjava.core.Vertx
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author gyam
 */

@ExtendWith(VertxExtension::class)
internal class DocStorageVerticleTest {
    private lateinit var vertx: Vertx
    private lateinit var docStorage: IDocStorage

    @BeforeEach
    fun setUp(context: VertxTestContext) {
        docStorage = mock()
        vertx = Vertx.vertx()
        vertx.delegate.deployVerticle(DocStorageVerticle(docStorage), context.succeeding {
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
        val expected = listOf(Doc("basicName", ZonedDateTime.now()))
        docStorage.stub {
            on { listAll() }.doReturn(Single.just(expected))
        }

        // Exercise
        vertx.eventBus().rxSend<JsonObject>(DEvent.LIST_DOCS.toString(), NoMessage().jsonify()).subscribe({ res ->
            assertThat(res.body()).isEqualTo(DocListResponse(expected).jsonify())
            async.flag()
        }, context::failNow)
    }
}