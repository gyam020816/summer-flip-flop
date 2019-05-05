package eu.ha3.x.sff.system.ledger

import eu.ha3.x.sff.core.SystemException
import eu.ha3.x.sff.core.ledger.Account
import eu.ha3.x.sff.core.ledger.AccountNumber
import eu.ha3.x.sff.test.testBlocking
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * (Default template)
 * Created on 2019-01-24
 *
 * @author Ha3
 */
internal class InMemoryLedgerSystemTest {
    companion object {
        val SAMPLE_ACCOUNT_ID = AccountNumber("e5d4442c-15a1-41cb-9983-6f36753bc633")
    }

    private val SUT = InMemoryLedgerSystem()

    @Test
    internal fun `it should create a new ledger`() = testBlocking {
        val expected = Account(SAMPLE_ACCOUNT_ID, "My Bank")

        // Exercise
        SUT.newAccount(expected)

        // Verify
        assertThat(SUT.accounts).containsExactly(expected)
    }

    @Test
    internal fun `it should not create ledger with duplicate IDs`() = testBlocking {
        // Exercise
        SUT.newAccount(Account(SAMPLE_ACCOUNT_ID, "My Bank"))
        val error = assertThrows<SystemException> { runBlocking { SUT.newAccount(Account(SAMPLE_ACCOUNT_ID, "My Bank")) } }

        // Verify
        assertThat(error).hasMessage("Duplicate AccountNumber $SAMPLE_ACCOUNT_ID")
    }
}