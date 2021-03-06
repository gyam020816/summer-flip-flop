package eu.ha3.x.sff.connector.vertx.coroutine

import com.fasterxml.jackson.databind.ObjectMapper
import eu.ha3.x.sff.api.SDocStorage
import eu.ha3.x.sff.connector.vertx.CodecObjectMapper
import eu.ha3.x.sff.connector.vertx.DEvent
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocCreateRequest
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.CoroutineVerticle

/**
 * (Default template)
 * Created on 2018-10-11
 *
 * @author Ha3
 */
class SDocStorageVertxBinder(mapper: ObjectMapper = CodecObjectMapper.mapper) {
    val appendToDocsBinder = SBinder(mapper, DEvent.APPEND_TO_DOCS.address(), DocCreateRequest::class.java, Doc::class.java)
    val listDocsBinder = SBinder(mapper, DEvent.LIST_DOCS.address(), NoMessage::class.java, DocListResponse::class.java)

    inner class Verticle(private val concrete: SDocStorage) : CoroutineVerticle() {
        override suspend fun start() {
            appendToDocsBinder.ofCoroutine { doc ->
                concrete.appendToDocs(doc)

            }.registerAnswerer(vertx)

            listDocsBinder.ofCoroutine {
                concrete.listAll()

            }.registerAnswerer(vertx)
        }
    }

    inner class QuestionSender(vertx: Vertx) : SDocStorage {
        private val appendToDocsFn = appendToDocsBinder.questionSender(vertx)
        private val listDocsFn = listDocsBinder.questionSender(vertx)

        override suspend fun appendToDocs(request: DocCreateRequest): Doc = appendToDocsFn(request)
        override suspend fun listAll(): DocListResponse = listDocsFn(NoMessage)
    }
}