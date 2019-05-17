package eu.ha3.x.sff.api

import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocCreateRequest
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.system.SDocSystem
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2019-01-14
 *
 * @author Ha3
 */
class SuspendedDocStorage(private val docSystem: SDocSystem, val currentTimeFn: () -> ZonedDateTime = ZonedDateTime::now) : SDocStorage {
    override suspend fun listAll(): DocListResponse {
        return docSystem.listAll()
    }

    override suspend fun appendToDocs(request: DocCreateRequest): Doc {
        val document = Doc(request.name, now())
        return docSystem.appendToDocs(document)
                .let { document }
    }

    private fun now() = currentTimeFn()
}
