package eu.ha3.x.sff.connector.vertx

import com.fasterxml.jackson.databind.ObjectMapper
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
import eu.ha3.x.sff.system.RxDocPersistenceSystem
import io.reactivex.Single
import io.vertx.core.Future
import io.vertx.rxjava.core.AbstractVerticle
import io.vertx.rxjava.core.eventbus.EventBus

/**
 * (Default template)
 * Created on 2018-10-11
 *
 * @author Ha3
 */
class RxDocSystemVertx(mapper: ObjectMapper = CodecObjectMapper.mapper) {
    val appendToDocsBinder = Binder(mapper, DEvent.SYSTEM_APPEND_TO_DOCS.address(), Doc::class.java, NoMessage::class.java)
    val listDocsBinder = Binder(mapper, DEvent.SYSTEM_LIST_DOCS.address(), NoMessage::class.java, DocListResponse::class.java)

    inner class Verticle(private val concrete: RxDocPersistenceSystem) : AbstractVerticle() {
        override fun start(fut: Future<Void>) {
            appendToDocsBinder.ofSingle { doc ->
                concrete.appendToDocs(doc)

            }.registerAnswerer(vertx.eventBus())

            listDocsBinder.ofSingle {
                concrete.listAll()

            }.registerAnswerer(vertx.eventBus())

            fut.complete()
        }
    }

    inner class QuestionSender(eventBus: EventBus) : RxDocPersistenceSystem {
        private val appendToDocsFn = appendToDocsBinder.questionSender(eventBus)
        private val listDocsFn = listDocsBinder.questionSender(eventBus)

        override fun appendToDocs(doc: Doc): Single<NoMessage> = appendToDocsFn(doc)
        override fun listAll(): Single<DocListResponse> = listDocsFn(NoMessage)
    }
}