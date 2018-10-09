package eu.ha3.x.sff.connector.vertx

import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec
import io.vertx.core.json.JsonObject

/**
 * (Default template)
 * Created on 2018-10-10
 *
 * @author Ha3
 */
class DJsonObjectMessageCodec : MessageCodec<DJsonObject, DJsonObject> {
    override fun encodeToWire(buffer: Buffer, dJsonObject: DJsonObject) {
        val encoded = Buffer.buffer(Jsonify.mapper.writeValueAsBytes(dJsonObject.inner))
        buffer.appendInt(encoded.length())
        buffer.appendBuffer(encoded)
    }

    override fun decodeFromWire(pos: Int, buffer: Buffer): DJsonObject {
        var pos = pos
        val length = buffer.getInt(pos)
        pos += 4
        return DJsonObject(JsonObject(buffer.slice(pos, pos + length)))
    }

    override fun transform(dJsonObject: DJsonObject): DJsonObject = dJsonObject.copy()

    override fun name(): String = "djsonobject"

    override fun systemCodecID(): Byte = -1
}
