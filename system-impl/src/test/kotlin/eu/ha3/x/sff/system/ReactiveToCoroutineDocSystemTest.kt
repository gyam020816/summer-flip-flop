package eu.ha3.x.sff.system

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.DomainException
import eu.ha3.x.sff.core.NoMessage
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2019-01-15
 *
 * @author Ha3
 */
internal class ReactiveToCoroutineDocSystemTest {
    private val mockDocSystem = mock<SDocPersistenceSystem>()
    private val SUT = ReactiveToCoroutineDocPersistenceSystem(mockDocSystem)

    @Test
    internal fun `it should delegate call to coroutine listAll`() {
        val expected = DocListResponse(listOf(Doc("basicName", ZonedDateTime.now())))
        mockDocSystem.stub {
            onBlocking { listAll() }.doReturn(expected)
        }

        // Exercise
        val result = SUT.listAll().blockingGet()

        // Verify
        assertThat(result).isEqualTo(expected)
    }

    @Test
    internal fun `it should carry exceptions from coroutine listAll`() {
        mockDocSystem.stub {
            onBlocking { listAll() }.doThrow(DomainException("expected"))
        }

        // Exercise and Verify
        assertThatThrownBy { SUT.listAll().blockingGet() }
                .isInstanceOf(DomainException::class.java)
    }

    @Test
    internal fun `it should delegate call to coroutine appendToDocs`() {
        val input = Doc("basicName", ZonedDateTime.now())
        mockDocSystem.stub {
            onBlocking { appendToDocs(input) }.doReturn(NoMessage)
        }

        // Exercise
        val result = SUT.appendToDocs(input).blockingGet()

        // Verify
        assertThat(result).isEqualTo(NoMessage)
    }

    @Test
    internal fun `it should carry exceptions from coroutine appendToDocs`() {
        val input = Doc("basicName", ZonedDateTime.now())
        mockDocSystem.stub {
            onBlocking { appendToDocs(input) }.doThrow(DomainException("expected"))
        }

        // Exercise and Verify
        assertThatThrownBy { SUT.appendToDocs(input).blockingGet() }
                .isInstanceOf(DomainException::class.java)
    }
}