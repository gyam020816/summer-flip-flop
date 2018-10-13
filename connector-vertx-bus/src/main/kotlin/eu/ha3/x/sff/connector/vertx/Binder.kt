package eu.ha3.x.sff.connector.vertx

import io.reactivex.Single
import io.vertx.rxjava.core.AbstractVerticle
import io.vertx.rxjava.core.eventbus.EventBus

/**
 * (Default template)
 * Created on 2018-10-12
 *
 * @author Ha3
 */
interface DBinder
class Binder<Q : Any, A : Any>(
        private val address: String,
        private val questionClass: Class<Q>,
        private val answerClass: Class<A>,
        private val errorHandler: (DAnswerer<*>) -> (Throwable) -> Unit = { answerer -> { error ->
            answerer.message.fail(500, "")
        }}
) : AbstractVerticle(), DBinder {
    fun ofSingle(boundFn: (Q) -> Single<A>) = BSingle(boundFn)

    interface DBind<B> {
        fun registerAnswerer(bus: EventBus)
        fun bind(bus: EventBus): B
    }

    fun questionSender(q: Q): Single<A> = questionSender()(q)

    fun questionSender(): (Q) -> Single<A> = { question ->
        Single.create<A> { handler ->
            vertx.eventBus().dsSendBound(address, question, answerClass).subscribe({ res ->
                handler.onSuccess(res.answer)

            }, handler::onError);
        }
    }

    inner class BSingle(private val boundFn: (Q) -> Single<A>): DBind<(Q) -> Single<A>> {
        override fun registerAnswerer(bus: EventBus) {
            bus.dsAnswererBound(address, { question: Q, answerer: DAnswerer<A> ->
                boundFn.invoke(question).subscribe(answerer::answer, errorHandler.invoke(answerer))
            }, questionClass)
        }

        override fun bind(bus: EventBus): (Q) -> Single<A> {
            registerAnswerer(bus)
            return questionSender()
        }
    }
}