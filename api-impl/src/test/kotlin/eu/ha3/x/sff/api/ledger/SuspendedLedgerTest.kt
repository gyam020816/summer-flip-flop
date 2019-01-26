package eu.ha3.x.sff.api.ledger

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import eu.ha3.x.sff.core.ledger.Account
import eu.ha3.x.sff.core.ledger.AccountNumber
import eu.ha3.x.sff.system.ledger.SLedgerSystem
import eu.ha3.x.sff.test.testBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * (Default template)
 * Created on 2019-01-24
 *
 * @author Ha3
 */
internal class SuspendedLedgerTest {
    companion object {
        val SAMPLE_ACCOUNT_ID = AccountNumber("e5d4442c-15a1-41cb-9983-6f36753bc633")
    }

    private val mockLedgerSystem: SLedgerSystem = mock()

    private val SUT = LedgerImpl(mockLedgerSystem, { SAMPLE_ACCOUNT_ID })

    @Test
    internal fun `it should open a ledger`() = testBlocking {
        mockLedgerSystem.stub {
            onBlocking { newAccount(Account(SAMPLE_ACCOUNT_ID, "My Bank")) }.thenReturn(Unit)
        }

        // Exercise
        val result = SUT.execute(Ledger.OpenAccount("My Bank"))

        // Verify
        assertThat(result).isEqualTo(SLedgerEvent.AccountOpened(SAMPLE_ACCOUNT_ID))
    }
}