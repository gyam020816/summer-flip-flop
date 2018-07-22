package eu.ha3.x.sff.connector.spring

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

/**
 * (Default template)
 * Created on 2018-07-22
 *
 * @author Ha3
 */
@SpringBootTest(classes = [Application::class])
public class TwilioControllerTest : AControllerTest() {
    @Test
    fun `it should return an XML content type`() {
        mockMvc.perform(get("/endpoint/text"))
                .andExpect(status().isOk)
                .andExpect(header().string("content-type", "application/xml;charset=UTF-8"))
                .andExpect(content().string("<>"))
    }
}