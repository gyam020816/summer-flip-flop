package eu.ha3.x.sff.connector.vertx

import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec

class NoMessage

class NoMessageCodec : MessageCodec<NoMessage, NoMessage> {
    override fun name() = this::class.java.simpleName;

    override fun transform(send: NoMessage?): NoMessage = send!!

    override fun systemCodecID(): Byte = -1

    override fun decodeFromWire(pos: Int, buffer: Buffer?) = NoMessage()

    override fun encodeToWire(buffer: Buffer?, s: NoMessage?) {
    }

}