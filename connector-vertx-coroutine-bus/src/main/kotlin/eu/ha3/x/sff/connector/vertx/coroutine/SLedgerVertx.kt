package eu.ha3.x.sff.connector.vertx.coroutine

import com.fasterxml.jackson.databind.ObjectMapper
import eu.ha3.x.sff.api.ledger.Ledger
import eu.ha3.x.sff.api.ledger.SLedger
import eu.ha3.x.sff.api.ledger.SLedgerEvent
import eu.ha3.x.sff.connector.vertx.CodecObjectMapper
import eu.ha3.x.sff.connector.vertx.DEvent
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.CoroutineVerticle

/**
 * (Default template)
 * Created on 2018-10-11
 *
 * @author Ha3
 */
class SLedgerVertx(mapper: ObjectMapper = CodecObjectMapper.mapper) {
    val openAccount = SBinder(mapper, DEvent.LEDGER_OPEN_ACCOUNT.address(), Ledger.OpenAccount::class.java, SLedgerEvent.AccountOpened::class.java)

    inner class Verticle(private val concrete: SLedger) : CoroutineVerticle() {
        override suspend fun start() {
            openAccount.ofSuspended { openAccount ->
                concrete.execute(openAccount)

            }.registerAnswerer(vertx)
        }
    }

    inner class QuestionSender(vertx: Vertx) : SLedger {
        private val openAccountFn = openAccount.questionSender(vertx)

        override suspend fun <E : SLedgerEvent> execute(command: Ledger<E>): E {
            return when (command) {
                is Ledger.OpenAccount -> openAccountFn(command) as E
                is Ledger.EnterTransactionForm -> TODO()
            }
        }
    }
}