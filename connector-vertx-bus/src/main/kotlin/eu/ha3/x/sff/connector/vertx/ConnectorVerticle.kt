package eu.ha3.x.sff.connector.vertx

/**
 * (Default template)
 * Created on 2018-08-30
 *
 * @author Ha3
 */
import io.vertx.core.AbstractVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.eventbus.Message

class ConnectorVerticle : AbstractVerticle() {
    private val s1 = "STUB1"
    private val s2 = "STUB2"
    private val s3 = "STUB3"

    override fun start(fut: Future<Void>) {
        vertx.eventBus().apply {
            consumer(VEvent.GREETING.toString()) { msg: Message<String> ->
                send(VEvent.GREETING_SEQUEL.toString(), s3) { res: AsyncResult<Message<String>> ->
                    if (res.succeeded()) {
                        msg.reply(s1)

                    } else {
                        msg.fail(0, "Failed")
                    }
                }
            }
            consumer(VEvent.GREETING_SEQUEL.toString()) { msg: Message<String> ->
                msg.reply(s2)
            }
        }

        fut.complete()
    }
}
