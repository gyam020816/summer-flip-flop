package eu.ha3.x.sff.connector.vertx

import io.vertx.core.json.JsonArray
import io.vertx.kotlin.core.json.JsonObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2018-10-09
 *
 * @author Ha3
 */
internal class JsonifyTest {
    data class ExampleModel(val someZdt: ZonedDateTime, val someList: List<String>, val someString: String)

    @Test
    internal fun `it should convert model to json`() {
        val model = JsonifyTest.MODEL

        // Exercise
        val result = model.jsonify()

        // Verify
        result.inner.apply {
            assertThat(getString("someZdt")).isEqualTo("2010-12-30T22:00:04.123Z")
            assertThat(getJsonArray("someList")).containsExactly("a", "b")
            assertThat(getString("someString")).isEqualTo("hello")
        }
    }

    @Test
    internal fun `it should convert json to model`() {
        val expected = JsonifyTest.MODEL
        val json = DJsonObject(JsonObject()
                .put("someZdt", "2010-12-30T22:00:04.123Z")
                .put("someList", JsonArray(listOf("a", "b")))
                .put("someString", "hello"))

        // Exercise
        val result = json.dejsonify(ExampleModel::class.java)

        // Verify
        assertThat(result).isEqualTo(expected)
    }

    @Test
    internal fun `it should convert non-utc model to json`() {
        val model = JsonifyTest.MODEL_ZONED_PARIS

        // Exercise
        val result = model.jsonify()

        // Verify
        result.inner.apply {
            assertThat(getString("someZdt")).isEqualTo("2010-12-30T22:00:04.123+01:00")
            assertThat(getJsonArray("someList")).containsExactly("a", "b")
            assertThat(getString("someString")).isEqualTo("hello")
        }
    }

    @Test
    internal fun `it should convert json to non-utc model`() {
        val expected = JsonifyTest.MODEL_ZONED_PARIS
        val json = DJsonObject(JsonObject()
                .put("someZdt", "2010-12-30T22:00:04.123+01:00")
                .put("someList", JsonArray(listOf("a", "b")))
                .put("someString", "hello"))

        // Exercise
        val result = json.dejsonify(ExampleModel::class.java)

        // Verify
        assertThat(result).isEqualTo(expected)
    }

    companion object {
        val MODEL = ExampleModel(
                ZonedDateTime.of(2010, 12, 30, 22, 0, 4, 123_000_000, ZoneOffset.UTC).withFixedOffsetZone(),
                listOf("a", "b"),
                "hello"
        )
        val MODEL_ZONED_PARIS = ExampleModel(
                ZonedDateTime.of(2010, 12, 30, 22, 0, 4, 123_000_000, ZoneId.of("Europe/Paris")).withFixedOffsetZone(),
                listOf("a", "b"),
                "hello"
        )
    }
}