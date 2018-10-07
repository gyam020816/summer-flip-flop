package eu.ha3.x.sff.connector.vertx

import eu.ha3.x.sff.api.IDocStorage
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author gyam
 */
class DocStorageVerticle(private val delegate: IDocStorage) : AbstractVerticle() {
    override fun start(fut: Future<Void>) {
        vertx.eventBus().apply {
            consumer<JsonObject>(DEvent.LIST_DOCS.address(), ::handler)
        }

        fut.complete()
    }

    private fun handler(msg: Message<JsonObject>) {
        delegate.listAll().subscribe({ result ->
            msg.reply(JsonObject.mapFrom(DocListResponse(result)))

        }, { error ->
            msg.fail(500, "")
        })
    }
}

