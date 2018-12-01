package eu.ha3.x.sff.connector.vertx

import eu.ha3.x.sff.api.IDocStorage
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocCreateRequest
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
import io.reactivex.Single
import io.vertx.core.Future
import io.vertx.rxjava.core.AbstractVerticle
import io.vertx.rxjava.core.eventbus.EventBus

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author gyam
 */
class DocStorageVertx {
    val appendToDocsBinder = Binder(Jsonify.mapper, DEvent.APPEND_TO_DOCS.address(), DocCreateRequest::class.java, Doc::class.java)
    val listDocsBinder = Binder(Jsonify.mapper, DEvent.LIST_DOCS.address(), NoMessage::class.java, DocListResponse::class.java)

    inner class Verticle(private val concrete: IDocStorage) : AbstractVerticle() {
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

    inner class QuestionSender(private val eventBus: EventBus) : IDocStorage {
        private val appendToDocsFn = appendToDocsBinder.questionSender(eventBus)
        private val listDocsFn = listDocsBinder.questionSender(eventBus)

        override fun appendToDocs(doc: DocCreateRequest): Single<Doc> = appendToDocsFn(doc)
        override fun listAll(): Single<DocListResponse> = listDocsFn(NoMessage())
    }
}