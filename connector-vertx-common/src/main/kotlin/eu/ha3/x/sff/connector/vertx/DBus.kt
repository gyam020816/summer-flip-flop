package eu.ha3.x.sff.connector.vertx

import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.Single
import io.vertx.rxjava.core.eventbus.EventBus
import io.vertx.rxjava.core.eventbus.Message

/**
 * (Default template)
 * Created on 2018-10-11
 *
 * @author Ha3
 */
typealias DConsumer<Q, A> = (question: Q, answerer: DEventBus.DAnswerer<A>) -> Unit;

class DEventBus(val eventBus: EventBus, objectMapper: ObjectMapper) {
    val mapper = DMapper(objectMapper)

    inner class DAnswer<T>(val answer: T, val message: Message<DJsonObject>)
    inner class DAnswerer<A : Any>(val message: Message<DJsonObject>) {
        fun answer(answer: A) {
            message.reply(mapper.asAnswer(answer))
        }
    }
    inline fun <A> dsSend(address: String, question: Any, answerClass: Class<A>): Single<DAnswer<A>> = Single.create { handler ->
        eventBus.rxSend<DJsonObject>(address, mapper.asQuestion(question)).subscribe({ success ->
            handler.onSuccess(DAnswer(mapper.interpretAs(success.body(), answerClass), success))

        }, handler::onError);
    }
    inline fun <reified A> dsSend(address: String, question: Any): Single<DAnswer<A>> = dsSend(address, question, A::class.java)

    inline fun <Q, A : Any> dsConsumer(address: String, crossinline consumerFn: DConsumer<Q, A>, questionClass: Class<Q>) {
        eventBus.consumer<DJsonObject>(address) { handle ->
            consumerFn(mapper.interpretAs(handle.body(), questionClass), DAnswerer(handle))
        }
    }
    inline fun <reified Q, A : Any> dsConsumer(address: String, crossinline consumerFn: DConsumer<Q, A>) = dsConsumer(address, consumerFn, Q::class.java);
}