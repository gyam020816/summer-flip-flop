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
class DBound(val eventBus: EventBus, objectMapper: ObjectMapper) {
    val mapper = DMapper(objectMapper)

    inner class DAnswer<T>(val answer: T, val message: Message<DJsonObject>)
    inner class DAnswerer<A : Any>(val message: Message<DJsonObject>) {
        fun answer(answer: A) {
            message.reply(mapper.asAnswer(answer))
        }
    }
    inline fun <reified A> dsSend(address: String, question: Any): Single<DAnswer<A>> = Single.create { handler ->
        eventBus.rxSend<DJsonObject>(address, mapper.asQuestion(question)).subscribe({ success ->
            handler.onSuccess(DAnswer(mapper.interpretAs(success.body(), A::class.java), success))

        }, handler::onError);
    }
    inline fun <reified Q> dsConsumer(address: String, crossinline consumerFn: (question: Q, message: Message<DJsonObject>) -> Unit) {
        eventBus.consumer<DJsonObject>(address) { handle ->
            consumerFn(mapper.interpretAs(handle.body(), Q::class.java), handle)
        }
    }
    inline fun <reified Q, reified A : Any> dsAnswerer(address: String, crossinline consumerFn: (question: Q, answerer: DAnswerer<A>) -> Unit) {
        eventBus.consumer<DJsonObject>(address) { handle ->
            consumerFn(mapper.interpretAs(handle.body(), Q::class.java), DAnswerer(handle))
        }
    }

    inline fun <A> dsSendBound(address: String, question: Any, answerClass: Class<A>): Single<DAnswer<A>> = Single.create { handler ->
        eventBus.rxSend<DJsonObject>(address, mapper.asQuestion(question)).subscribe({ success ->
            handler.onSuccess(DAnswer(mapper.interpretAs(success.body(), answerClass), success))

        }, handler::onError);
    }
    inline fun <Q> dsConsumerBound(address: String, crossinline consumerFn: (question: Q, message: Message<DJsonObject>) -> Unit, questionClass: Class<Q>) {
        eventBus.consumer<DJsonObject>(address) { handle ->
            consumerFn(mapper.interpretAs(handle.body(), questionClass), handle)
        }
    }
    inline fun <Q, A : Any> dsAnswererBound(address: String, crossinline consumerFn: (question: Q, answerer: DAnswerer<A>) -> Unit, questionClass: Class<Q>) {
        eventBus.consumer<DJsonObject>(address) { handle ->
            consumerFn(mapper.interpretAs(handle.body(), questionClass), DAnswerer(handle))
        }
    }
}