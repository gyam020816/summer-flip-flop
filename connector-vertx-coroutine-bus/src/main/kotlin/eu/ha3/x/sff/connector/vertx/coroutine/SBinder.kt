package eu.ha3.x.sff.connector.vertx.coroutine

import com.fasterxml.jackson.databind.ObjectMapper
import eu.ha3.x.sff.connector.vertx.SEventBus
import io.vertx.core.Vertx

/**
 * (Default template)
 * Created on 2018-10-12
 *
 * @author Ha3
 */
typealias QASuspendFunction<Q, A> = suspend (Q) -> A

interface SDBinder
class SBinder<Q : Any, A : Any>(
        private val objectMapper: ObjectMapper,
        private val address: String,
        private val questionClass: Class<Q>,
        private val answerClass: Class<A>,
        private val errorHandlerGenFn: (SEventBus.SAnswerer<*>) -> (Throwable) -> Unit = { answerer -> { error ->
            answerer.message.fail(500, "")
        }}
) : SDBinder {
    fun ofCoroutine(boundFn: QASuspendFunction<Q, A>) = BCoroutine(boundFn)

    interface SDBind<B> {
        fun registerAnswerer(vertx: Vertx)
        fun bind(vertx: Vertx): B
    }

//    suspend fun questionSender(eventBus: EventBus, vertx: Vertx, q: Q): A = questionSender(eventBus, vertx)(q)

    fun questionSender(vertx: Vertx): QASuspendFunction<Q, A> = { question ->
        SEventBus(vertx, objectMapper).ssSend(address, question, answerClass).answer
    }

    inner class BCoroutine(private val boundFn: QASuspendFunction<Q, A>): SDBind<QASuspendFunction<Q, A>> {
        override fun registerAnswerer(vertx: Vertx) {
            SEventBus(vertx, objectMapper).ssConsumer(address, { question: Q, answerer: SEventBus.SAnswerer<A> ->
                try {
                    answerer.answer(boundFn(question))
                } catch (e: Exception) {
                    errorHandlerGenFn(answerer).invoke(e)
                }
            }, questionClass)
        }

        override fun bind(vertx: Vertx): QASuspendFunction<Q, A> {
            registerAnswerer(vertx)
            return questionSender(vertx)
        }
    }
}