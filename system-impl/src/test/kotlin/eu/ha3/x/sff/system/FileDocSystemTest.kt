
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.system.FileDocSystem
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

/**
 * (Default template)
 * Created on 2018-12-09
 *
 * @author Ha3
 */
internal class FileDocSystemTest {
    private val virtualFilesystem = Jimfs.newFileSystem(Configuration.unix()).apply {
        Files.createDirectories(getPath("some_subfolder"))
    }
    private val SUT = FileDocSystem(virtualFilesystem.getPath("some_subfolder"))

    @Test
    internal fun `it should be empty at first`() {
        // Verify
        assertThat(SUT.listAll().blockingGet()).isEqualTo(DocListResponse(emptyList()))
    }

    @Test
    internal fun `it should append to docs and return it (facade)`() {
        val item = Doc("a", ZonedDateTime.of(2000, 12, 1, 23, 40, 50, 0, ZoneOffset.UTC))

        // Exercise
        SUT.appendToDocs(item).blockingGet()

        // Verify
        assertThat(SUT.listAll().blockingGet()).isEqualTo(DocListResponse(listOf(item)))
    }

    @Test
    internal fun `it should append to docs and return them by createdAt property (facade)`() {
        val item2001 = Doc("a", ZonedDateTime.of(2001, 12, 1, 23, 40, 50, 0, ZoneOffset.UTC))
        val item1999 = Doc("a", ZonedDateTime.of(1999, 12, 1, 23, 40, 50, 0, ZoneOffset.UTC))
        val item2000 = Doc("a", ZonedDateTime.of(2000, 12, 1, 23, 40, 50, 0, ZoneOffset.UTC))

        // Exercise
        SUT.appendToDocs(item2001).blockingGet()
        SUT.appendToDocs(item1999).blockingGet()
        SUT.appendToDocs(item2000).blockingGet()

        // Verify
        assertThat(SUT.listAll().blockingGet()).isEqualTo(DocListResponse(listOf(item1999, item2000, item2001)))
    }

    @Test
    internal fun `it should be empty even if a subfolder has a file`() {
        val innerFolder = virtualFilesystem.getPath("some_subfolder").resolve("inner_folder")
        Files.createDirectories(innerFolder)
        Files.write(innerFolder.resolve(UUID.randomUUID().toString()), ByteArray(1))

        // Verify
        assertThat(SUT.listAll().blockingGet()).isEqualTo(DocListResponse(emptyList()))
    }
}