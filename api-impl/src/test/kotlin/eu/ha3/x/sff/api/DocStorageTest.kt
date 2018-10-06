package eu.ha3.x.sff.api

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import eu.ha3.x.sff.core.Doc
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
    @Test
    internal fun `it should list all docs from doc system`() {
        val expected = listOf(Doc("basicName", ZonedDateTime.now()))
        val docSystem = mock<IDocSystem> {
            on { listAll() }.doReturn(Single.just(expected))
        }
        val SUT = DocStorage(docSystem)

        // Exercise
        val result = SUT.listAll()

        // Verify
        result.test()
                .assertNoErrors()
                .assertValue(verify {
                    assertThat(this).isEqualTo(expected)
                })
    }
}