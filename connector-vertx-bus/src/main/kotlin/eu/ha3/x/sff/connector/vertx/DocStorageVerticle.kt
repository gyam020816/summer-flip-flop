package eu.ha3.x.sff.connector.vertx

import eu.ha3.x.sff.api.IDocStorage
import io.vertx.core.Future
import io.vertx.rxjava.core.AbstractVerticle
import io.vertx.rxjava.core.eventbus.Message

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author gyam
 */
class DocStorageVerticle(private val delegate: IDocStorage) : AbstractVerticle() {
    override fun start(fut: Future<Void>) {
        vertx.eventBus().apply {
            dsConsumer(DEvent.LIST_DOCS.address(), ::handler)
        }

        fut.complete()
    }

    private fun handler(noMessage: NoMessage, msg: Message<DJsonObject>) {
        delegate.listAll().subscribe({ result ->
            msg.reply(DocListResponse(result).asAnswer())

        }, { error ->
            msg.fail(500, "")
        })
    }
}

