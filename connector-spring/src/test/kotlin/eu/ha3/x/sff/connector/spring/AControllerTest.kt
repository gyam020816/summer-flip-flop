package eu.ha3.x.sff.connector.spring

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup
import org.springframework.web.context.WebApplicationContext

/**
 * (Default template)
 * Created on 2018-07-22
 *
 * @author Ha3
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [Application::class, PayloadAppConfig::class])
abstract class AControllerTest {
    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    protected lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build()
    }
}
