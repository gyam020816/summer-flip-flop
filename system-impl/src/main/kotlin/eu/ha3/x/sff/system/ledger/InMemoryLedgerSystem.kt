package eu.ha3.x.sff.system.ledger

import eu.ha3.x.sff.core.SystemException
import eu.ha3.x.sff.core.ledger.Account

/**
 * (Default template)
 * Created on 2019-01-24
 *
 * @author Ha3
 */
class InMemoryLedgerSystem : SLedgerSystem {
    @Deprecated("This should not be exposed")
    val accounts: MutableList<Account> = mutableListOf()

    override suspend fun newAccount(account: Account) {
        if (accounts.any { other -> other.accountNumber == account.accountNumber }) {
            throw SystemException("Duplicate AccountNumber ${account.accountNumber}")
        }
        accounts.add(account)
    }
}