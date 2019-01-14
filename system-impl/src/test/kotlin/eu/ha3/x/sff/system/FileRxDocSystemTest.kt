
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.system.RxDocSystemTestFacade
import eu.ha3.x.sff.system.FileRxDocSystem
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.util.*

/**
 * (Default template)
 * Created on 2018-12-09
 *
 * @author Ha3
 */
internal class FileRxDocSystemTest : RxDocSystemTestFacade<FileRxDocSystem> {
    private val virtualFilesystem = Jimfs.newFileSystem(Configuration.unix()).apply {
        Files.createDirectories(getPath("some_subfolder"))
    }
    private val SUT = FileRxDocSystem(virtualFilesystem.getPath("some_subfolder"))

    override fun SUT(): FileRxDocSystem = SUT

    @Test
    internal fun `it should be empty even if a subfolder has a file`() {
        val innerFolder = virtualFilesystem.getPath("some_subfolder").resolve("inner_folder")
        Files.createDirectories(innerFolder)
        Files.write(innerFolder.resolve(UUID.randomUUID().toString()), ByteArray(1))

        // Exercise
        val result = SUT.listAll().blockingGet()

        // Verify
        assertThat(result).isEqualTo(DocListResponse(emptyList()))
    }
}