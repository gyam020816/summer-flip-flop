package eu.ha3.x.sff.api

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocCreateRequest
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
import eu.ha3.x.sff.system.SDocSystem
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author gyam
 */
internal class SuspendedDocStorageTest {
    val mockDocSystem = mock<SDocSystem>()
    val mockCurrentTimeFn = mock<() -> ZonedDateTime>()
    val SUT = SuspendedDocStorage(mockDocSystem, currentTimeFn = mockCurrentTimeFn)

    @Test
    internal fun `it should list all docs from doc system`() = runBlocking {
        val expected = DocListResponse(listOf(Doc("basicName", ZonedDateTime.now())))
        mockDocSystem.stub {
            onBlocking { listAll() }.doReturn(expected)
        }

        // Exercise
        val result = SUT.listAll()

        // Verify
        assertThat(result).isEqualTo(expected)

    }.let { Unit }

    @Test
    internal fun `it should append a doc to the system`() = runBlocking {
        val currentTime = ZonedDateTime.now()
        val expected = Doc("basicName", currentTime)
        mockDocSystem.stub {
            onBlocking { appendToDocs(expected) }.doReturn(NoMessage)
        }
        mockCurrentTimeFn.stub {
            on { invoke() }.doReturn(currentTime)
        }

        // Exercise
        val result = SUT.appendToDocs(DocCreateRequest("basicName"))

        // Verify
        assertThat(result).isEqualTo(expected)

    }.let { Unit }
}