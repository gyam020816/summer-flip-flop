package eu.ha3.x.sff.system.ledger

import eu.ha3.x.sff.core.ledger.Account

/**
 * (Default template)
 * Created on 2019-01-24
 *
 * @author Ha3
 */
interface SLedgerSystem {
    suspend fun newAccount(account: Account)
}