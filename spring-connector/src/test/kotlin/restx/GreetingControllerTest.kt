package restx

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup
import org.springframework.web.context.WebApplicationContext
import java.nio.charset.StandardCharsets


/**
 * (Default template)
 * Created on 2018-07-18
 *
 * @author Ha3
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [Application::class, TestAppConfig::class])
public class GreetingControllerTest {
    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var mockPayloadStorage: IPayloadStorage;

    private lateinit var mockMvc: MockMvc;

    @BeforeEach
    public fun setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public fun `it should return a greeting`() {
        mockMvc.perform(get("/greeting"))
                .andExpect(status().isOk)
                .andExpect(content().string("""{"id":0,"content":"Hello, World"}"""))
    }

    @Test
    public fun `it should return a custom greeting`() {
        mockMvc.perform(
                get("/greeting?name=Gérald")
        )
                .andExpect(status().isOk)
                .andExpect(content().string("""{"id":0,"content":"Hello, Gérald"}"""))
    }

    @Test
    public fun `it should upload a payload and return its size`() {
        mockMvc.perform(fileUpload("/payloads").file(MockMultipartFile("file", "hello".toByteArray(StandardCharsets.UTF_8))))
                .andExpect(status().isCreated)
                .andExpect(content().string("""5"""))
    }

    @Test
    public fun `it should accept a payload without a file`() {
        mockMvc.perform(fileUpload("/payloads"))
                .andExpect(content().string("""-1"""))
    }

    @Test
    public fun `it should list all payloads`() {
        whenever(mockPayloadStorage.getAll())
                .thenReturn(listOf(somePayload()))

        mockMvc.perform(get("/payloads"))
                .andExpect(content().string("""[{"id":"ABCD"}]"""))
    }

    private fun somePayload() = Payload(GENERIC_ID)

    companion object {
        private val GENERIC_ID = "ABCD"
    }
}

@Profile("test")
@Configuration
open class TestAppConfig {
    @Bean
    @Primary
    public open fun payloadStorage(): IPayloadStorage = mock {
    }
}