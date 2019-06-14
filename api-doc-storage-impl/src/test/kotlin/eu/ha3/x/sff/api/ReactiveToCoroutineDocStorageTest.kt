package eu.ha3.x.sff.api

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import eu.ha3.x.sff.core.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2019-01-14
 *
 * @author Ha3
 */
internal class ReactiveToCoroutineDocStorageTest {
    private val mockDocStorage = mock<SDocStorage>()
    private val SUT = ReactiveToCoroutineDocStorage(mockDocStorage)

    @Test
    internal fun `it should delegate call to coroutine listAll`() {
        val expected = DocListResponse(listOf(Doc("basicName", ZonedDateTime.now())))
        mockDocStorage.stub {
            onBlocking { listAll() }.doReturn(expected)
        }

        // Exercise
        val result = SUT.listAll().blockingGet()

        // Verify
        assertThat(result).isEqualTo(expected)
    }

    @Test
    internal fun `it should carry exceptions from coroutine listAll`() {
        mockDocStorage.stub {
            onBlocking { listAll() }.doThrow(DomainException("expected"))
        }

        // Exercise and Verify
        assertThatThrownBy { SUT.listAll().blockingGet() }
                .isInstanceOf(DomainException::class.java)
    }

    @Test
    internal fun `it should delegate call to coroutine appendToDocs`() {
        val request = DocCreateRequest("basicName")
        val expected = Doc("basicName", ZonedDateTime.now())
        mockDocStorage.stub {
            onBlocking { appendToDocs(request) }.doReturn(expected)
        }

        // Exercise
        val result = SUT.appendToDocs(request).blockingGet()

        // Verify
        assertThat(result).isEqualTo(expected)
    }

    @Test
    internal fun `it should carry exceptions from coroutine appendToDocs`() {
        val request = DocCreateRequest("basicName")
        mockDocStorage.stub {
            onBlocking { appendToDocs(request) }.doThrow(DomainException("expected"))
        }

        // Exercise and Verify
        assertThatThrownBy { SUT.appendToDocs(request).blockingGet() }
                .isInstanceOf(DomainException::class.java)
    }

    @Test
    internal fun `it should delegate call to coroutine listPaginated`() {
        val expected = DocListResponse(listOf(Doc("basicName", ZonedDateTime.now())))
        mockDocStorage.stub {
            onBlocking { listPaginated(DocListPaginationRequest(1)) }.doReturn(expected)
        }

        // Exercise
        val result = SUT.listPaginated(DocListPaginationRequest(1)).blockingGet()

        // Verify
        assertThat(result).isEqualTo(expected)
    }

    @Test
    internal fun `it should carry exceptions from coroutine listPaginated`() {
        mockDocStorage.stub {
            onBlocking { listPaginated(DocListPaginationRequest(1)) }.doThrow(DomainException("expected"))
        }

        // Exercise and Verify
        assertThatThrownBy { SUT.listPaginated(DocListPaginationRequest(1)).blockingGet() }
              .isInstanceOf(DomainException::class.java)
    }
}