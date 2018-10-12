package eu.ha3.x.sff.connector.spring

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import eu.ha3.x.sff.api.IDocStorage
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.test.TestSample
import io.reactivex.Single
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.nio.charset.StandardCharsets

@Profile("test")
@Configuration
open class PayloadAppConfig {
    @Bean
    @Primary
    public open fun docStorage(): IDocStorage = mock()
}

@SpringBootTest(classes = [Application::class, PayloadAppConfig::class, JacksonMapper::class])
public class PayloadControllerTest : AControllerTest() {
    @Autowired
    lateinit var mockDocStorage: IDocStorage;

    @Test
    public fun `it should upload a doc and return its size`() {
        mockMvc.perform(fileUpload("/docs").file(MockMultipartFile("file", "hello".toByteArray(StandardCharsets.UTF_8))))
                .andExpect(status().isCreated)
                .andExpect(content().string("""5"""))
    }

    @Test
    public fun `it should accept a doc without a file`() {
        mockMvc.perform(fileUpload("/docs"))
                .andExpect(content().string("""-1"""))
    }

    @Test
    public fun `it should list all docs`() {
        whenever(mockDocStorage.listAll())
                .thenReturn(Single.just(DocListResponse(listOf(someDoc()))))

        mockMvc.perform(get("/docs"))
                .andExpect(content().string("""[{"name":"ABCD","createdAt":"${TestSample.zonedDateTimeSerialized}"}]"""))
    }

    private fun someDoc() = Doc(GENERIC_ID, TestSample.zonedDateTime)

    companion object {
        private val GENERIC_ID = "ABCD"
    }
}