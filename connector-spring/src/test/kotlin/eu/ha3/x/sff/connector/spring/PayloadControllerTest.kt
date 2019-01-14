package eu.ha3.x.sff.connector.spring

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import eu.ha3.x.sff.api.RxDocStorage
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocCreateRequest
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.test.TestSample
import io.reactivex.Single
import net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Profile("test")
@Configuration
open class PayloadAppConfig {
    @Bean
    @Primary
    public open fun docStorage(): RxDocStorage = mock()
}

@SpringBootTest(classes = [Application::class, PayloadAppConfig::class, JacksonMapper::class])
public class PayloadControllerTest : AControllerTest() {
    @Autowired
    lateinit var mockDocStorage: RxDocStorage;

    @Test
    public fun `it should accept a doc`() {
        whenever(mockDocStorage.appendToDocs(DocCreateRequest("someDoc")))
                .thenReturn(Single.just(Doc("someDoc", TestSample.zonedDateTime)))

        mockMvc.perform(post("/docs")
                .content("""{"name": "someDoc"}""")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().`is`(201))
                .andDo {
                    assertThatJson(it.response.contentAsString).isEqualTo("""{"name": "someDoc", "createdAt": "${TestSample.zonedDateTimeSerialized}"}""")
                }
    }

    @Test
    public fun `it should error with bad request`() {
        mockMvc.perform(post("/docs")
                .content("""{}""")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().`is`(400))
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