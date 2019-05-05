package eu.ha3.x.sff.api.ledger

import eu.ha3.x.sff.core.ledger.Account
import eu.ha3.x.sff.core.ledger.AccountNumber
import eu.ha3.x.sff.system.ledger.SLedgerSystem

/**
 * (Default template)
 * Created on 2019-01-24
 *
 * @author Ha3
 */
class LedgerImpl(
        private val ledgerSystem: SLedgerSystem,
        private val accountNumberGeneratorFn: suspend () -> AccountNumber
) : SLedger {
    override suspend fun <E : SLedgerEvent> execute(command: Ledger<E>): E {
        return when (command) {
            is Ledger.OpenAccount -> openAccount(command) as E
            is Ledger.EnterTransactionForm -> TODO()
        }
    }

    private suspend fun openAccount(request: Ledger.OpenAccount): SLedgerEvent.AccountOpened {
        val accountId = accountNumberGeneratorFn()
        ledgerSystem.newAccount(Account(accountId, request.familiarName))

        return SLedgerEvent.AccountOpened(accountId)
    }
}