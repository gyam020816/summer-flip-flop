package eu.ha3.x.sff.json

import eu.ha3.x.sff.core.DSROperator
import eu.ha3.x.sff.core.DSRSource
import eu.ha3.x.sff.core.DSRTerminalElement
import net.javacrumbs.jsonunit.assertj.JsonAssertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

/**
 * (Default template)
 * Created on 2019-01-16
 *
 * @author Ha3
 */
internal class DSROperatorMixinTest {
    val SUT = KObjectMapper.newInstance()
            .apply(SearchModelsMixin::doRegisterMixins)

    @Test
    internal fun `it should serialize with mixins (simple)`() {
        // Exercise
        val json = SUT.writeValueAsString(SAMPLE_DATA_SIMPLE)

        // Verify
        JsonAssertions.assertThatJson(json).isEqualTo(SAMPLE_JSON_SIMPLE.trimIndent())
    }

    @Test
    internal fun `it should deserialize with mixins (simple)`() {
        // Exercise
        val data = SUT.readValue(SAMPLE_JSON_SIMPLE, DSROperator::class.java)

        // Verify
        assertThat(data).isEqualTo(SAMPLE_DATA_SIMPLE)
    }

    @Test
    internal fun `it should serialize with mixins (complex)`() {
        // Exercise
        val json = SUT.writeValueAsString(SAMPLE_DATA_COMPLEX)

        // Verify
        JsonAssertions.assertThatJson(json).isEqualTo(SAMPLE_JSON_COMPLEX.trimIndent())
    }

    @Test
    internal fun `it should deserialize with mixins`() {
        // Exercise
        val data = SUT.readValue(SAMPLE_JSON_COMPLEX, DSROperator::class.java)

        // Verify
        assertThat(data).isEqualTo(SAMPLE_DATA_COMPLEX)
    }

    companion object {
        val SAMPLE_JSON_SIMPLE = """
{
  "op": "and",
  "elements": [
    {
      "op": "always",
      "value": true
    },
    {
      "op": "or",
      "elements": [
        {
          "op": "always",
          "value": false
        }
      ]
    }
  ]
}"""

        val SAMPLE_DATA_SIMPLE = DSROperator.And(listOf(
                DSROperator.IsAlways(true),
                DSROperator.Or(listOf(
                        DSROperator.IsAlways(false)
                ))
        ))


        val SAMPLE_JSON_COMPLEX = """
{
  "op": "and",
  "elements": [
    {
      "op": "always",
      "value": true
    },
    {
      "op": "or",
      "elements": [
        {
          "op": "always",
          "value": false
        }
      ]
    },
    {
      "op": "equals",
      "key": {
        "kind": "singleElementKey",
        "match": "userName"
      },
      "value": {
        "type": "text",
        "value": "John DOE"
      }
    },
    {
      "op": "equals",
      "key": {
        "kind": "singleElementKey",
        "match": "accountAgeYears"
      },
      "value": {
        "type": "integerNumber",
        "value": 4
      }
    },
    {
      "op": "equals",
      "key": {
        "kind": "singleElementKey",
        "match": "balance"
      },
      "value": {
        "type": "decimalNumber",
        "value": 2.331
      }
    }
  ]
}"""

        val SAMPLE_DATA_COMPLEX = DSROperator.And(listOf(
                DSROperator.IsAlways(true),
                DSROperator.Or(listOf(
                        DSROperator.IsAlways(false)
                )),
                DSROperator.Equals(
                        DSRSource.SingleElementKey("userName"),
                        DSRTerminalElement.Text("John DOE")
                ),
                DSROperator.Equals(
                        DSRSource.SingleElementKey("accountAgeYears"),
                        DSRTerminalElement.IntegerNumber(4)
                ),
                DSROperator.Equals(
                        DSRSource.SingleElementKey("balance"),
                        DSRTerminalElement.DecimalNumber(BigDecimal("2.331"))
                )
        ))
    }
}