package eu.ha3.x.sff.connector.vertx.coroutine

import com.fasterxml.jackson.databind.ObjectMapper
import eu.ha3.x.sff.connector.vertx.CodecObjectMapper
import eu.ha3.x.sff.connector.vertx.DEvent
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
import eu.ha3.x.sff.system.SDocPersistenceSystem
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.CoroutineVerticle

/**
 * (Default template)
 * Created on 2018-10-11
 *
 * @author Ha3
 */
class SDocPersistenceSystemVertxBinder(mapper: ObjectMapper = CodecObjectMapper.mapper) {
    val appendToDocsBinder = SBinder(mapper, DEvent.SYSTEM_APPEND_TO_DOCS.address(), Doc::class.java, NoMessage::class.java)
    val listDocsBinder = SBinder(mapper, DEvent.SYSTEM_LIST_DOCS.address(), NoMessage::class.java, DocListResponse::class.java)

    inner class Verticle(private val concrete: SDocPersistenceSystem) : CoroutineVerticle() {
        override suspend fun start() {
            appendToDocsBinder.ofCoroutine { doc ->
                concrete.appendToDocs(doc)

            }.registerAnswerer(vertx)

            listDocsBinder.ofCoroutine {
                concrete.listAll()

            }.registerAnswerer(vertx)
        }
    }

    inner class QuestionSender(vertx: Vertx) : SDocPersistenceSystem {
        private val appendToDocsFn = appendToDocsBinder.questionSender(vertx)
        private val listDocsFn = listDocsBinder.questionSender(vertx)

        override suspend fun appendToDocs(doc: Doc): NoMessage = appendToDocsFn(doc)
        override suspend fun listAll(): DocListResponse = listDocsFn(NoMessage)
    }
}