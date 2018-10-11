package eu.ha3.x.sff.connector.vertx

import io.reactivex.Single
import io.vertx.rxjava.core.eventbus.EventBus
import io.vertx.rxjava.core.eventbus.Message

/**
 * (Default template)
 * Created on 2018-10-11
 *
 * @author Ha3
 */
inline fun <reified A> EventBus.dsSend(address: String, question: Any): Single<A> = Single.create { handler ->
    rxSend<DJsonObject>(address, question.jsonify()).subscribe({ success ->
        handler.onSuccess(success.body().dejsonify(A::class.java))

    }, handler::onError);
}
inline fun <reified Q> EventBus.dsConsumer(address: String, crossinline consumerFn: (Q, Message<DJsonObject>) -> Unit) {
    this.consumer<DJsonObject>(address) { handle ->
        consumerFn(handle.body().dejsonify(Q::class.java), handle)
    }
}