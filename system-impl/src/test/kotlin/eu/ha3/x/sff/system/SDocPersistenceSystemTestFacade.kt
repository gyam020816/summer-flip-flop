package eu.ha3.x.sff.system

import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.test.testBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2019-01-13
 *
 * @author Ha3
 */
interface SDocPersistenceSystemTestFacade<T : SDocPersistenceSystem> {
    fun SUT(): T

    @Test
    fun `it should be empty at first (facade)`() = testBlocking {
        // Exercise
        val result = SUT().listAll()

        // Verify
        assertThat(result).isEqualTo(DocListResponse(emptyList()))
    }

    @Test
    fun `it should append to docs and return it (facade)`() = testBlocking {
        val item = Doc("a", ZonedDateTime.of(2000, 12, 1, 23, 40, 50, 0, ZoneOffset.UTC))

        // Exercise
        SUT().appendToDocs(item)
        val result = SUT().listAll()

        // Verify
        assertThat(result).isEqualTo(DocListResponse(listOf(item)))
    }

    @Test
    fun `it should append to docs and return it, preserving the time zone (facade)`() = testBlocking {
        val item = Doc("a", ZonedDateTime.of(2000, 12, 1, 23, 40, 50, 0, ZoneOffset.of("+07:15")))

        // Exercise
        SUT().appendToDocs(item)
        val result = SUT().listAll()

        // Verify
        assertThat(result).isEqualTo(DocListResponse(listOf(item)))
    }

    @Test
    fun `it should append to docs and return them by createdAt property (facade)`() = testBlocking {
        val item2001 = Doc("a", ZonedDateTime.of(2001, 12, 1, 23, 40, 50, 0, ZoneOffset.UTC))
        val item1999 = Doc("a", ZonedDateTime.of(1999, 12, 1, 23, 40, 50, 0, ZoneOffset.UTC))
        val item2000 = Doc("a", ZonedDateTime.of(2000, 12, 1, 23, 40, 50, 0, ZoneOffset.UTC))

        // Exercise
        SUT().appendToDocs(item2001)
        SUT().appendToDocs(item1999)
        SUT().appendToDocs(item2000)
        val result = SUT().listAll()

        // Verify
        assertThat(result).isEqualTo(DocListResponse(listOf(item1999, item2000, item2001)))
    }
}