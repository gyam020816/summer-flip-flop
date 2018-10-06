package eu.ha3.x.sff.connector.vertx

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.stub
import eu.ha3.x.sff.api.IDocStorage
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import io.reactivex.Single
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author gyam
 */

@RunWith(VertxUnitRunner::class)
internal class DocStorageVerticleTest {
    private lateinit var vertx: Vertx
    private lateinit var docStorage: IDocStorage

    @Before
    fun setUp(context: TestContext) {
        docStorage = mock()
        vertx = Vertx.vertx()
        vertx.deployVerticle(DocStorageVerticle(docStorage),
                context.asyncAssertSuccess<String>())
    }

    @After
    fun tearDown(context: TestContext) {
        vertx.close(context.asyncAssertSuccess())
    }

    @Test
    fun `it should delegate listAll`(context: TestContext) {
        val async = context.async(1)
        val expected = listOf(Doc("basicName", ZonedDateTime.now()))
        docStorage.stub {
            on { listAll() }.doReturn(Single.just(expected))
        }

        // Exercise
        vertx.eventBus().send<JsonObject>(DEvent.LIST_DOCS.toString(), "") { res ->
            assertThat(res.result().body()).isEqualTo(JsonObject.mapFrom(DocListResponse(expected)))
            async.countDown()
        }
    }
}