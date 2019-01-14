package eu.ha3.x.sff.system

import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2019-01-13
 *
 * @author Ha3
 */
interface RxDocSystemTestFacade<T : RxDocSystem> {
    fun SUT(): T;

    @Test
    fun `it should be empty at first (facade)`() {
        // Exercise
        val result = SUT().listAll().blockingGet()

        // Verify
        Assertions.assertThat(result).isEqualTo(DocListResponse(emptyList()))
    }

    @Test
    fun `it should append to docs and return it (facade)`() {
        val item = Doc("a", ZonedDateTime.of(2000, 12, 1, 23, 40, 50, 0, ZoneOffset.UTC))

        // Exercise
        SUT().appendToDocs(item).blockingGet()
        val result = SUT().listAll().blockingGet()

        // Verify
        Assertions.assertThat(result).isEqualTo(DocListResponse(listOf(item)))
    }

    @Test
    fun `it should append to docs and return them by createdAt property (facade)`() {
        val item2001 = Doc("a", ZonedDateTime.of(2001, 12, 1, 23, 40, 50, 0, ZoneOffset.UTC))
        val item1999 = Doc("a", ZonedDateTime.of(1999, 12, 1, 23, 40, 50, 0, ZoneOffset.UTC))
        val item2000 = Doc("a", ZonedDateTime.of(2000, 12, 1, 23, 40, 50, 0, ZoneOffset.UTC))

        // Exercise
        SUT().appendToDocs(item2001).blockingGet()
        SUT().appendToDocs(item1999).blockingGet()
        SUT().appendToDocs(item2000).blockingGet()
        val result = SUT().listAll().blockingGet()

        // Verify
        Assertions.assertThat(result).isEqualTo(DocListResponse(listOf(item1999, item2000, item2001)))
    }
}