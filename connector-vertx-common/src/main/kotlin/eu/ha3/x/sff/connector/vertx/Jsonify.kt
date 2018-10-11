package eu.ha3.x.sff.connector.vertx

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import java.util.*

/**
 * (Default template)
 * Created on 2018-10-09
 *
 * @author Ha3
 */
object Jsonify {
    val mapper: ObjectMapper = ObjectMapper()
    val prettyMapper: ObjectMapper = ObjectMapper()

    init {
        listOf(mapper, prettyMapper).forEach {
            it.registerKotlinModule()
            it.registerModule(JavaTimeModule())
            it.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            it.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false); // This avoid [UTC] somehow, see tests

            val module = SimpleModule()
            module.addSerializer(JsonObject::class.java, JsonObjectSerializer)
            module.addSerializer(JsonArray::class.java, JsonArraySerializer)
            module.addSerializer(ByteArray::class.java, ByteArraySerializer)
            mapper.registerModule(module)
        }
        prettyMapper.configure(SerializationFeature.INDENT_OUTPUT, true)
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
fun <T> DJsonObject.dejsonify(klass: Class<T>): T = Jsonify.mapper.convertValue<T>(this.inner.map, klass)
fun <T> String.dejsonifyByParsing(klass: Class<T>): T = Jsonify.mapper.readValue(this, klass)
fun Any.jsonify(): DJsonObject = DJsonObject(JsonObject(Jsonify.mapper.convertValue(this, Map::class.java) as Map<String, Any>))
fun Any.jsonifyToString(): String = Jsonify.mapper.writeValueAsString(this)
fun Any.jsonifyToPrettyString(): String = Jsonify.prettyMapper.writeValueAsString(this)
