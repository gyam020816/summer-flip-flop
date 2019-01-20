package eu.ha3.x.sff.json

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import eu.ha3.x.sff.core.DSROperator
import eu.ha3.x.sff.core.DSRSource
import eu.ha3.x.sff.core.DSRTerminalElement
import java.math.BigDecimal

/**
 * (Default template)
 * Created on 2019-01-16
 *
 * @author Ha3
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "op")
@JsonSubTypes(
    JsonSubTypes.Type(value = DSROperator.IsAlways::class, name = "always"),
    JsonSubTypes.Type(value = DSROperator.And::class, name = "and"),
    JsonSubTypes.Type(value = DSROperator.Or::class, name = "or"),
    JsonSubTypes.Type(value = DSROperator.Equals::class, name = "equals")
)
sealed class DSROperatorMixin {
    data class IsAlwaysMixin(val op: String?, val value: Boolean)
    data class AndMixin(val op: String?, val elements: List<DSROperator>)
    data class OrMixin(val op: String?, val elements: List<DSROperator>)
    data class EqualsMixin(val op: String?, val key: DSRSource.SingleElementKey, val value: DSRTerminalElement)
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "kind"
)
@JsonSubTypes(
        JsonSubTypes.Type(value = DSRSource.SingleElementKey::class, name = "singleElementKey")
)
sealed class DSRSourceMixin {
    data class SingleElementKeyMixin(val kind: String?, val match: String)
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes(
        JsonSubTypes.Type(value = DSRTerminalElement.Text::class, name = "text"),
        JsonSubTypes.Type(value = DSRTerminalElement.IntegerNumber::class, name = "integerNumber"),
        JsonSubTypes.Type(value = DSRTerminalElement.DecimalNumber::class, name = "decimalNumber")
)
sealed class DSRTerminalElementMixin {
    data class TextMixin(val type: String?, val value: String)
    data class IntegerNumberMixin(val type: String?, val value: Long)
    data class DecimalNumberMixin(val type: String?, val value: BigDecimal)
}

object SearchModelsMixin {
    fun doRegisterMixins(mapper: ObjectMapper) {
        mapper.apply {
            addMixIn(DSROperator::class.java, DSROperatorMixin::class.java)
            addMixIn(DSROperator.IsAlways::class.java, DSROperatorMixin.IsAlwaysMixin::class.java)
            addMixIn(DSROperator.And::class.java, DSROperatorMixin.AndMixin::class.java)
            addMixIn(DSROperator.Or::class.java, DSROperatorMixin.OrMixin::class.java)
            addMixIn(DSROperator.Equals::class.java, DSROperatorMixin.EqualsMixin::class.java)

            addMixIn(DSRSource::class.java, DSRSourceMixin::class.java)
            addMixIn(DSRSource.SingleElementKey::class.java, DSRSourceMixin.SingleElementKeyMixin::class.java)

            addMixIn(DSRTerminalElement::class.java, DSRTerminalElementMixin::class.java)
            addMixIn(DSRTerminalElement.Text::class.java, DSRTerminalElementMixin.TextMixin::class.java)
            addMixIn(DSRTerminalElement.IntegerNumber::class.java, DSRTerminalElementMixin.IntegerNumberMixin::class.java)
            addMixIn(DSRTerminalElement.DecimalNumber::class.java, DSRTerminalElementMixin.DecimalNumberMixin::class.java)

        }
    }
}