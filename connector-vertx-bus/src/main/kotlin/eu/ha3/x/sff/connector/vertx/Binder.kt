package eu.ha3.x.sff.connector.vertx

import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.Single
import io.vertx.rxjava.core.eventbus.EventBus

/**
 * (Default template)
 * Created on 2018-10-12
 *
 * @author Ha3
 */
interface DBinder
class Binder<Q : Any, A : Any>(
        private val objectMapper: ObjectMapper,
        private val address: String,
        private val questionClass: Class<Q>,
        private val answerClass: Class<A>,
        private val errorHandler: (DBound.DAnswerer<*>) -> (Throwable) -> Unit = { answerer -> { error ->
            answerer.message.fail(500, "")
        }}
) : DBinder {
    fun ofSingle(boundFn: (Q) -> Single<A>) = BSingle(boundFn)

    interface DBind<B> {
        fun registerAnswerer(bus: EventBus)
        fun bind(bus: EventBus): B
    }

    fun questionSender(eventBus: EventBus, q: Q): Single<A> = questionSender(eventBus)(q)

    fun questionSender(bus: EventBus): (Q) -> Single<A> = { question ->
        Single.create<A> { handler ->
            DBound(bus, objectMapper).dsSendBound(address, question, answerClass).subscribe({ res ->
                handler.onSuccess(res.answer)

            }, handler::onError);
        }
    }

    inner class BSingle(private val boundFn: (Q) -> Single<A>): DBind<(Q) -> Single<A>> {
        override fun registerAnswerer(bus: EventBus) {
            DBound(bus, objectMapper).dsAnswererBound(address, { question: Q, answerer: DBound.DAnswerer<A> ->
                boundFn.invoke(question).subscribe(answerer::answer, errorHandler.invoke(answerer))
            }, questionClass)
        }

        override fun bind(bus: EventBus): (Q) -> Single<A> {
            registerAnswerer(bus)
            return questionSender(bus)
        }
    }
}