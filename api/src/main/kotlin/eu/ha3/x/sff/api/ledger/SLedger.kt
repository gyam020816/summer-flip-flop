package eu.ha3.x.sff.api.ledger

import eu.ha3.x.sff.core.ledger.AccountNumber
import java.math.BigDecimal

/**
 * (Default template)
 * Created on 2019-01-24
 *
 * @author Ha3
 */
interface SLedger {
    suspend fun <E : SLedgerEvent> execute(command: Ledger<E>): E
}

sealed class Ledger<T : SLedgerEvent> {
    data class OpenAccount(val familiarName: String) : Ledger<SLedgerEvent.AccountOpened>() {
        init {
            if (familiarName.isEmpty()) throw IllegalArgumentException("familiarName must not be empty")
        }
    }

    data class EnterTransactionForm(val accountNumber: AccountNumber, val payee: Payee, val debit: BigDecimal) : Ledger<SLedgerEvent.TransactionEntered>()
}

sealed class SLedgerEvent {
    data class AccountOpened(val accountNumber: AccountNumber) : SLedgerEvent()
    data class TransactionEntered(val accountNumber: AccountNumber) : SLedgerEvent()
}

data class Payee(val temp: Unit)
