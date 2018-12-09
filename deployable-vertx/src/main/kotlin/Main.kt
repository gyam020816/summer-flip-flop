
import eu.ha3.x.sff.api.DocStorage
import eu.ha3.x.sff.connector.vertx.*
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
import eu.ha3.x.sff.system.IDocSystem
import io.reactivex.Single
import io.vertx.rxjava.core.Vertx
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author gyam
 */
fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    vertx.delegate.eventBus().registerDefaultCodec(DJsonObject::class.java, DJsonObjectMessageCodec())

    val bus = vertx.eventBus()

    val system = DocSystemVertx()
    val storage = DocStorageVertx()

    val concreteDocStorage = DocStorage(system.QuestionSender(bus))

    val verticles = listOf(
            system.Verticle(object : IDocSystem {
                override fun appendToDocs(doc: Doc) = Single.just(NoMessage)
                override fun listAll() = Single.just(DocListResponse(listOf(Doc("hello", ZonedDateTime.now()))))
            }),
            storage.Verticle(concreteDocStorage),
            WebVerticle(storage.QuestionSender(bus))
    )
    verticles.forEach(vertx.delegate::deployVerticle)
}