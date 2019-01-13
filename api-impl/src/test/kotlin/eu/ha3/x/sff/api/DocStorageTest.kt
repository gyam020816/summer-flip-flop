package eu.ha3.x.sff.api

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocCreateRequest
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
import eu.ha3.x.sff.system.IDocSystem
import eu.ha3.x.sff.test.verify
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author gyam
 */
internal class DocStorageTest {
    val mockDocSystem = mock<IDocSystem>()
    val mockCurrentTimeFn = mock<() -> ZonedDateTime>()
    val SUT = DocStorage(mockDocSystem, currentTimeFn = mockCurrentTimeFn)

    @Test
    internal fun `it should list all docs from doc system`() {
        val expected = DocListResponse(listOf(Doc("basicName", ZonedDateTime.now())))
        mockDocSystem.stub {
            on { listAll() }.doReturn(Single.just(expected))
        }

        // Exercise
        val result = SUT.listAll()

        // Verify
        result.test()
                .assertNoErrors()
                .assertValue(verify {
                    assertThat(this).isEqualTo(expected)
                })
    }

    @Test
    internal fun `it should append a doc to the system`() {
        val currentTime = ZonedDateTime.now()
        val expected = Doc("basicName", currentTime)
        mockDocSystem.stub {
            on { appendToDocs(expected) }.doReturn(Single.just(NoMessage))
        }
        mockCurrentTimeFn.stub {
            on { invoke() }.doReturn(currentTime)
        }

        // Exercise
        val result = SUT.appendToDocs(DocCreateRequest("basicName"))

        // Verify
        result.test()
                .assertNoErrors()
                .assertValue(verify {
                    assertThat(this).isEqualTo(expected)
                })
    }
}