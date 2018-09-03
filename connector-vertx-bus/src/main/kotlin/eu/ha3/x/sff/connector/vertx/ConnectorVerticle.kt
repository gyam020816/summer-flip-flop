package eu.ha3.x.sff.connector.vertx

/**
 * (Default template)
 * Created on 2018-08-30
 *
 * @author Ha3
 */
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future

class ConnectorVerticle : AbstractVerticle() {
    private val s1 = "STUB1"
    private val s2 = "STUB2"
    private val s3 = "STUB3"

    override fun start(fut: Future<Void>) {
        vertx.eventBus().apply {
            consumer<String>(VEvent.GREETING.toString()) { msg ->
                send<String>(VEvent.GREETING_SEQUEL.toString(), s3) { res ->
                    if (res.succeeded()) {
                        msg.reply(s1)

                    } else {
                        msg.fail(0, "Failed")
                    }
                }
            }
            consumer<String>(VEvent.GREETING_SEQUEL.toString()) { msg ->
                msg.reply(s2)
            }
        }

        fut.complete()
    }
}
