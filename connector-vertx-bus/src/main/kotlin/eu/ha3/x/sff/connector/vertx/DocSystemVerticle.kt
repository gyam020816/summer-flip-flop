package eu.ha3.x.sff.connector.vertx

import eu.ha3.x.sff.api.IDocSystem
import io.vertx.core.Future
import io.vertx.rxjava.core.AbstractVerticle
import io.vertx.rxjava.core.eventbus.Message

/**
 * (Default template)
 * Created on 2018-10-11
 *
 * @author gyam
 */
class DocSystemVerticle(private val delegate: IDocSystem) : AbstractVerticle() {
    override fun start(fut: Future<Void>) {
        vertx.eventBus().apply {
            consumer<DJsonObject>(DEvent.SYSTEM_LIST_DOCS.address(), ::handler)
        }

        fut.complete()
    }

    private fun handler(msg: Message<DJsonObject>) {
        delegate.listAll().subscribe({ result ->
            msg.reply(DocListResponse(result).jsonify())

        }, { error ->
            msg.fail(500, "")
        })
    }
}