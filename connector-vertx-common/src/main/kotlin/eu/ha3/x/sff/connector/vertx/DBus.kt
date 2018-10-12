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
data class DAnswer<T>(val answer: T, val message: Message<DJsonObject>)
data class DAnswerer<A : Any>(val message: Message<DJsonObject>) {
    fun answer(answer: A) {
        message.reply(answer.asAnswer())
    }
}
inline fun <reified A> EventBus.dsSend(address: String, question: Any): Single<DAnswer<A>> = Single.create { handler ->
    rxSend<DJsonObject>(address, question.asQuestion()).subscribe({ success ->
        handler.onSuccess(DAnswer(success.body().interpretAs(A::class.java), success))

    }, handler::onError);
}
inline fun <reified Q> EventBus.dsConsumer(address: String, crossinline consumerFn: (question: Q, message: Message<DJsonObject>) -> Unit) {
    this.consumer<DJsonObject>(address) { handle ->
        consumerFn(handle.body().interpretAs(Q::class.java), handle)
    }
}
inline fun <reified Q, reified A : Any> EventBus.dsAnswerer(address: String, crossinline consumerFn: (question: Q, answerer: DAnswerer<A>) -> Unit) {
    this.consumer<DJsonObject>(address) { handle ->
        consumerFn(handle.body().interpretAs(Q::class.java), DAnswerer(handle))
    }
}