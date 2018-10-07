package eu.ha3.x.sff.connector.vertx

import eu.ha3.x.sff.core.Doc
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec
import io.vertx.core.json.Json

interface IMessage
interface IResponse

data class DocListResponse(val data: List<Doc>) : IResponse
data class SystemDocListResponse(val data: List<Doc>) : IResponse

class ResponseCodec<T>(private val decodingClass: Class<T>) : MessageCodec<T, T> {
    override fun name() = this::class.java.simpleName;

    override fun transform(send: T): T = send!!

    override fun systemCodecID(): Byte = -1

    override fun decodeFromWire(pos: Int, buffer: Buffer): T {
        val length = buffer.getInt(pos)
        val start = pos + 4
        return Json.decodeValue(buffer.slice(start, start + length), decodingClass)
    }

    override fun encodeToWire(buffer: Buffer, s: T) {
        val json = Json.encodeToBuffer(s)
        buffer.appendInt(json.length())
        buffer.appendBuffer(json)
    }
}