package eu.ha3.deployable
import eu.ha3.x.sff.api.ReactiveDocStorage
import eu.ha3.x.sff.connector.vertx.*
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
import eu.ha3.x.sff.system.RxDocSystem
import eu.ha3.x.sff.system.SuspendedToRxDocSystem
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

    val system = SDocSystemVertx()
    val storage = RxDocStorageVertx()

    val concreteDocStorage = ReactiveDocStorage(SuspendedToRxDocSystem(system.QuestionSender(bus)))

    val verticles = listOf(
            system.Verticle(object : RxDocSystem {
                override fun appendToDocs(doc: Doc) = Single.just(NoMessage)
                override fun listAll() = Single.just(DocListResponse(listOf(Doc("hello", ZonedDateTime.now()))))
            }),
            storage.Verticle(concreteDocStorage),
            ReactiveWebVerticle(storage.QuestionSender(bus))
    )
    verticles.forEach(vertx.delegate::deployVerticle)
}