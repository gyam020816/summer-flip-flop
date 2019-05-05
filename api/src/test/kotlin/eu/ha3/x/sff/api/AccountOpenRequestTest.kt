package eu.ha3.x.sff.api

import eu.ha3.x.sff.api.ledger.Ledger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * (Default template)
 * Created on 2019-01-24
 *
 * @author Ha3
 */
internal class AccountOpenRequestTest {
    @Test
    internal fun `it should create an instance`() {
        assertThat(Ledger.OpenAccount("some name").familiarName).isEqualTo("some name")
    }

    @Test
    internal fun `it must have a non-empty name`() {
        val error = assertThrows<IllegalArgumentException> { Ledger.OpenAccount("") }
        assertThat(error.message).isEqualTo("familiarName must not be empty")
    }
}