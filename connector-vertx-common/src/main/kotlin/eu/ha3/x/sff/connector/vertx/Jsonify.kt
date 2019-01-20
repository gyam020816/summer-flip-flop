package eu.ha3.x.sff.connector.vertx

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import eu.ha3.x.sff.json.KObjectMapper
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import java.util.*

/**
 * (Default template)
 * Created on 2018-10-09
 *
 * @author Ha3
 */
object CodecObjectMapper {
    val mapper: ObjectMapper = KObjectMapper.newInstance()

    init {
        mapper.apply {
            val module = SimpleModule()
            module.addSerializer(JsonObject::class.java, JsonObjectSerializer)
            module.addSerializer(JsonArray::class.java, JsonArraySerializer)
            module.addSerializer(ByteArray::class.java, ByteArraySerializer)
            registerModule(module)
        }
    }

    private object JsonArraySerializer : JsonSerializer<JsonArray>() {
        override fun serialize(value: JsonArray, jgen: JsonGenerator, provider: SerializerProvider) {
            jgen.writeObject(value.list)
        }
    }

    private object JsonObjectSerializer : JsonSerializer<JsonObject>() {
        override fun serialize(value: JsonObject, jgen: JsonGenerator, provider: SerializerProvider) {
            jgen.writeObject(value.map)
        }
    }

    private object ByteArraySerializer : JsonSerializer<ByteArray>() {
        private val BASE64: Base64.Encoder = Base64.getEncoder()

        override fun serialize(value: ByteArray, jgen: JsonGenerator, provider: SerializerProvider) {
            jgen.writeString(this.BASE64.encodeToString(value))
        }
    }
}

data class DJsonObject(val inner: JsonObject)
class DMapper(private val mapper: ObjectMapper) {
    @Deprecated("Use interpretAs() instead")
    fun <T> dejsonify(dJsonObject: DJsonObject, klass: Class<T>): T = mapper.convertValue<T>(dJsonObject.inner.map, klass)
    fun <T> interpretAs(dJsonObject: DJsonObject, klass: Class<T>): T = mapper.convertValue<T>(dJsonObject.inner.map, klass)
    fun <T> dejsonifyByParsing(s: String, klass: Class<T>): T = mapper.readValue(s, klass)
    @Deprecated("Use asQuestion() or asAnswer() instead")
    fun jsonify(any: Any): DJsonObject = DJsonObject(JsonObject(mapper.convertValue(any, Map::class.java) as Map<String, Any>))
    fun asQuestion(any: Any): DJsonObject = DJsonObject(JsonObject(mapper.convertValue(any, Map::class.java) as Map<String, Any>))
    fun asAnswer(any: Any): DJsonObject = DJsonObject(JsonObject(mapper.convertValue(any, Map::class.java) as Map<String, Any>))
    fun jsonifyToString(any: Any): String = mapper.writeValueAsString(any)
}