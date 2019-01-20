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
typealias QAFunction<Q, A> = (Q) -> Single<A>

interface DBinder
class Binder<Q : Any, A : Any>(
        private val objectMapper: ObjectMapper,
        private val address: String,
        private val questionClass: Class<Q>,
        private val answerClass: Class<A>,
        private val errorHandlerGenFn: (DEventBus.DAnswerer<*>) -> (Throwable) -> Unit = { answerer -> { error ->
            answerer.message.fail(500, "")
        }}
) : DBinder {
    fun ofSingle(boundFn: QAFunction<Q, A>) = BSingle(boundFn)

    interface DBind<B> {
        fun registerAnswerer(bus: EventBus)
        fun bind(bus: EventBus): B
    }

    fun questionSender(eventBus: EventBus, q: Q): Single<A> = questionSender(eventBus)(q)

    fun questionSender(bus: EventBus): QAFunction<Q, A> = { question ->
        Single.create<A> { handler ->
            DEventBus(bus, objectMapper).dsSend(address, question, answerClass).subscribe({ res ->
                handler.onSuccess(res.answer)

            }, handler::onError)
        }
    }

    inner class BSingle(private val boundFn: QAFunction<Q, A>): DBind<QAFunction<Q, A>> {
        override fun registerAnswerer(bus: EventBus) {
            DEventBus(bus, objectMapper).dsConsumer(address, { question: Q, answerer: DEventBus.DAnswerer<A> ->
                boundFn(question).subscribe(answerer::answer, errorHandlerGenFn(answerer))
            }, questionClass)
        }

        override fun bind(bus: EventBus): QAFunction<Q, A> {
            registerAnswerer(bus)
            return questionSender(bus)
        }
    }
}