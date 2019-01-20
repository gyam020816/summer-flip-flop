package eu.ha3.x.sff.connector.vertx

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * (Default template)
 * Created on 2018-10-11
 *
 * @author Ha3
 */
typealias SConsumer<Q, A> = suspend (question: Q, answerer: SEventBus.SAnswerer<A>) -> Unit

class SEventBus(val vertx: Vertx, objectMapper: ObjectMapper) {
    val mapper = DMapper(objectMapper)

    inner class SAnswer<T>(val answer: T, val message: Message<DJsonObject>)
    inner class SAnswerer<A : Any>(val message: Message<DJsonObject>) {
        fun answer(answer: A) {
            message.reply(mapper.asAnswer(answer))
        }
    }
    inline suspend fun <A> ssSend(address: String, question: Any, answerClass: Class<A>): SAnswer<A> {
        val success = awaitResult<Message<DJsonObject>> { handler ->
            vertx.eventBus().send(address, mapper.asQuestion(question), handler)
        }

        return SAnswer(mapper.interpretAs(success.body(), answerClass), success)

    }
    inline suspend fun <reified A> ssSend(address: String, question: Any): SAnswer<A> = ssSend(address, question, A::class.java)

    inline fun <Q, A : Any> ssConsumer(address: String, noinline consumerFn: SConsumer<Q, A>, questionClass: Class<Q>) {
        vertx.eventBus().consumer<DJsonObject>(address) { handle ->
            GlobalScope.launch(vertx.dispatcher()) {
                consumerFn(mapper.interpretAs(handle.body(), questionClass), SAnswerer(handle))
            }
        }
    }
    inline fun <reified Q, A : Any> ssConsumer(address: String, noinline consumerFn: SConsumer<Q, A>) = ssConsumer(address, consumerFn, Q::class.java)
}