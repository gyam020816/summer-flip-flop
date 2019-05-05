package eu.ha3.x.sff.core.ledger

/**
 * (Default template)
 * Created on 2019-01-24
 *
 * @author Ha3
 */
data class AccountNumber(val number: String) {
    init {
        if (number.isEmpty()) throw IllegalArgumentException("number must not be empty")
    }
}
data class Account(val accountNumber: AccountNumber, val familiarName: String)