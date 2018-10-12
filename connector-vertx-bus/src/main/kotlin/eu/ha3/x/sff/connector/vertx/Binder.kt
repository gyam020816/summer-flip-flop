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
        private val boundFn: (Q) -> Single<A>,
        private val errorHandler: (DAnswerer<*>) -> (Throwable) -> Unit = { answerer -> { error ->
            answerer.message.fail(500, "")
        }}
) : AbstractVerticle(), DBinder {

    fun registerAnswerer(bus: EventBus) {
        bus.dsAnswererBound(address, { question: Q, answerer: DAnswerer<A> ->
            boundFn.invoke(question).subscribe(answerer::answer, errorHandler.invoke(answerer))
        }, questionClass)
    }

    fun questionner(): (Q) -> Single<A> = { question ->
        Single.create<A> { handler ->
            vertx.eventBus().dsSendBound(address, question, answerClass).subscribe({ res ->
                handler.onSuccess(res.answer)

            }, handler::onError);
        }
    }

    companion object {
        inline fun <reified Q : Any, reified A: Any> produce(bus: EventBus, address: String, noinline boundFn: (Q) -> Single<A>): (Q) -> Single<A> {
            val storage = Binder(address, Q::class.java, A::class.java, boundFn)
            storage.registerAnswerer(bus)
            return storage.questionner()
        }
    }
}