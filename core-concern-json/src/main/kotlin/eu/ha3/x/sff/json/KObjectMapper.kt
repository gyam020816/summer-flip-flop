package eu.ha3.x.sff.json
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

/**
 * (Default template)
 * Created on 2018-11-10
 *
 * @author Ha3
 */
object KObjectMapper {
    fun newInstance() = ObjectMapper().apply {
        registerKotlinModule()
        registerModule(JavaTimeModule())
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }
}