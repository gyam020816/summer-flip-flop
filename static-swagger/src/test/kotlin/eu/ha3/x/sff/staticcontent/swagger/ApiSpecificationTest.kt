package eu.ha3.x.sff.staticcontent.swagger

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * (Default template)
 * Created on 2019-05-17
 *
 * @author Ha3
 */
internal class ApiSpecificationTest {
    @Test
    internal fun `it should create a new instance`() {
        // Exercise
        val spec = ApiSpecification.newInstance()

        // Verify
        assertThat(spec.paths)
                .isNotNull()
                .isNotEmpty()
    }
}