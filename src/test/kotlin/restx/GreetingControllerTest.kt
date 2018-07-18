package restx

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit4.SpringRunner
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
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [Application::class])
public class GreetingControllerTest {
    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    private lateinit var mockMvc: MockMvc;

    @Before
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
}