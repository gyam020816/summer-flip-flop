package eu.ha3.x.sff.system.postgres
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonMappingException
import net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2018-11-10
 *
 * @author Ha3
 */
internal class KObjectMapperTest {
    private val SUT = KObjectMapper.newInstance()

    @Test
    internal fun `it should deserialize`() {
        // Exercise
        val result = SUT.readValue(NOMINAL_JSON, SampleModel::class.java)

        // Verify
        assertThat(result).isEqualTo(NOMINAL_MODEL)
    }

    @Test
    internal fun `it should serialize with no extra nulls`() {
        // Exercise
        val result = SUT.writeValueAsString(NOMINAL_MODEL)

        // Verify
        assertThatJson(result).isEqualTo(NOMINAL_JSON)
    }

    /**
     * Kotlin's Int is assumed to be considered a primitive by Jackson,
     * which needs [DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES].
     */
    @Test
    internal fun `it should not deserialize if number is missing`() {
        val input = """{
            "subModel" : {
              "number" : 400
            }
        }"""

        // Exercise and Verify
        assertThatThrownBy { SUT.readValue(input, SampleModel::class.java) }
                .isInstanceOf(JsonMappingException::class.java)
                .hasMessageStartingWith("Missing required creator property 'number'")
    }

    @Test
    internal fun `it should deserialize utc model`() {
        // Exercise
        val result = SUT.readValue(ZDT_UTC_JSON, SampleZdt::class.java)

        // Verify
        assertThatJson(result).isEqualTo(ZDT_UTC_MODEL)
    }


    @Test
    internal fun `it should serialize utc model`() {
        // Exercise
        val result = SUT.writeValueAsString(ZDT_UTC_MODEL)

        // Verify
        assertThatJson(result).isEqualTo(ZDT_UTC_JSON)
    }

    @Test
    internal fun `it should deserialize non-utc model`() {
        // Exercise
        val result = SUT.readValue(ZDT_PARIS_JSON, SampleZdt::class.java)

        // Verify
        assertThatJson(result).isEqualTo(ZDT_PARIS_MODEL)
    }

    @Test
    internal fun `it should serialize non-utc model by discarding zone information`() {
        // Exercise
        val result = SUT.writeValueAsString(ZDT_PARIS_MODEL)

        // Verify
        assertThatJson(result).isEqualTo(ZDT_PARIS_JSON)
    }

    private data class SampleModel(val number: Int, val floatOpt: Float? = null, val subModel: SampleModel? = null)
    private data class SampleZdt(val zdt: ZonedDateTime)

    private companion object {
        private val NOMINAL_MODEL = SampleModel(
                number = 300,
                floatOpt = 1.3f,
                subModel = SampleModel(number = 400)
        )
        private const val NOMINAL_JSON = """{
                "number" : 300,
                "floatOpt" : 1.3,
                "subModel" : {
                  "number" : 400
                }
            }"""

        private val ZDT_UTC_MODEL = SampleZdt(ZonedDateTime.of(2010, 12, 30, 22, 0, 4, 123_000_000, ZoneOffset.UTC).withFixedOffsetZone())
        private const val ZDT_UTC_JSON = """{
                "zdt" : "2010-12-30T22:00:04.123Z"
            }"""

        private val ZDT_PARIS_MODEL = SampleZdt(ZonedDateTime.of(2010, 12, 30, 22, 0, 4, 123_000_000, ZoneId.of("Europe/Paris")).withFixedOffsetZone())
        private const val ZDT_PARIS_JSON = """{
                "zdt" : "2010-12-30T22:00:04.123+01:00"
            }"""
    }
}