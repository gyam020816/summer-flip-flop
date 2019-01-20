package eu.ha3.x.sff.core

import java.math.BigDecimal

/**
 * (Default template)
 * Created on 2019-01-16
 *
 * @author Ha3
 */
sealed class DSROperator {
    data class IsAlways(val value: Boolean) : DSROperator()
    data class And(val elements: List<DSROperator>) : DSROperator()
    data class Or(val elements: List<DSROperator>) : DSROperator()
    data class Equals(val key: DSRSource.SingleElementKey, val value: DSRTerminalElement) : DSROperator()
}
sealed class DSRSource {
    data class SingleElementKey(val match: String) : DSRSource()
}
sealed class DSRTerminalElement {
    data class Text(val value: String) : DSRTerminalElement()
    data class IntegerNumber(val value: Long) : DSRTerminalElement()
    data class DecimalNumber(val value: BigDecimal) : DSRTerminalElement()
}