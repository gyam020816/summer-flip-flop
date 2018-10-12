package eu.ha3.x.sff.connector.vertx

import eu.ha3.x.sff.api.IDocStorage
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
        vertx.eventBus().apply {
            dsAnswerer(DEvent.LIST_DOCS.address(), ::handler)
        }

        fut.complete()
    }

    private fun handler(noMessage: NoMessage, msg: DAnswerer<DocListResponse>) {
        delegate.listAll().subscribe({ result ->
            msg.answer(DocListResponse(result))

        }, { error ->
            msg.message.fail(500, "")
        })
    }
}

