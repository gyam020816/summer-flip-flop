package eu.ha3.deployable
import eu.ha3.x.sff.api.SuspendedDocStorage
import eu.ha3.x.sff.connector.vertx.DJsonObject
import eu.ha3.x.sff.connector.vertx.DJsonObjectMessageCodec
import eu.ha3.x.sff.connector.vertx.SuspendedWebVerticle
import eu.ha3.x.sff.connector.vertx.coroutine.SDocStorageVertx
import eu.ha3.x.sff.connector.vertx.coroutine.SDocSystemVertx
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
import eu.ha3.x.sff.system.SDocSystem
import io.vertx.core.Vertx
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author gyam
 */
fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    vertx.eventBus().registerDefaultCodec(DJsonObject::class.java, DJsonObjectMessageCodec())

    val bus = vertx.eventBus()

    val system = SDocSystemVertx()
    val storage = SDocStorageVertx()

    val concreteDocStorage = SuspendedDocStorage(system.QuestionSender(vertx))

    val verticles = listOf(
            system.Verticle(object : SDocSystem {
                override suspend fun appendToDocs(doc: Doc) = NoMessage
                override suspend fun listAll() = DocListResponse(listOf(Doc("hello", ZonedDateTime.now())))
            }),
            storage.Verticle(concreteDocStorage),
            SuspendedWebVerticle(storage.QuestionSender(vertx))
    )
    verticles.forEach(vertx::deployVerticle)
}