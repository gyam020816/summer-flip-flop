package eu.ha3.x.sff.connector.vertx

import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
import eu.ha3.x.sff.system.IDocSystem
import io.reactivex.Single
import io.vertx.core.Future
import io.vertx.rxjava.core.AbstractVerticle

/**
 * (Default template)
 * Created on 2018-10-11
 *
 * @author gyam
 */
class DocSystemVerticle(private val delegate: IDocSystem) : AbstractVerticle() {
    val appendToDocs = Binder(Jsonify.mapper, DEvent.SYSTEM_APPEND_TO_DOCS.address(), Doc::class.java, NoMessage::class.java)
    val listDocs = Binder(Jsonify.mapper, DEvent.SYSTEM_LIST_DOCS.address(), NoMessage::class.java, DocListResponse::class.java)

    override fun start(fut: Future<Void>) {
        appendToDocs.ofSingle { doc ->
            delegate.appendToDocs(doc)

        }.registerAnswerer(vertx.eventBus())

        listDocs.ofSingle {
            delegate.listAll()

        }.registerAnswerer(vertx.eventBus())

        fut.complete()
    }

    inner class VersDocStorage : IDocSystem {
        override fun appendToDocs(doc: Doc): Single<NoMessage> = (appendToDocs.questionSender(vertx.eventBus()))(doc)
        override fun listAll(): Single<DocListResponse> = (listDocs.questionSender(vertx.eventBus()))(NoMessage())
    }
}