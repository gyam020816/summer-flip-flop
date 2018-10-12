package eu.ha3.x.sff.connector.vertx

import eu.ha3.x.sff.api.IDocStorage
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocCreateRequest
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
import io.reactivex.Single
import io.vertx.core.Future
import io.vertx.rxjava.core.AbstractVerticle

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author gyam
 */
class DocStorageVerticle(private val delegate: IDocStorage) : AbstractVerticle() {
    override fun start(fut: Future<Void>) {
        appendToDocs.ofSingle { doc ->
            delegate.appendToDocs(doc)

        }.bind(vertx.eventBus())

        listDocs.ofSingle {
            delegate.listAll()

        }.bind(vertx.eventBus())

        fut.complete()
    }

    object VersDocStorage : IDocStorage {
        override fun appendToDocs(doc: DocCreateRequest): Single<Doc> = appendToDocs.questionner()(doc)
        override fun listAll(): Single<DocListResponse> = listDocs.questionner()(NoMessage())
    }

    companion object {
        val appendToDocs = Binder(DEvent.APPEND_TO_DOCS.address(), DocCreateRequest::class.java, Doc::class.java)
        val listDocs = Binder(DEvent.LIST_DOCS.address(), NoMessage::class.java, DocListResponse::class.java)
    }
}

