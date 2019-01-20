package eu.ha3.x.sff.api

import eu.ha3.x.sff.core.*
import eu.ha3.x.sff.system.SDocSystem
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2019-01-14
 *
 * @author Ha3
 */
class SuspendedDocStorage(private val docSystem: SDocSystem, private val currentTimeFn: () -> ZonedDateTime = ZonedDateTime::now) : SDocStorage {
    override suspend fun search(queryInput: String): DocListResponse {
        val split = queryInput.split("=")
        val docSearch = DocSearch(DSROperator.Equals(DSRSource.SingleElementKey(split[0]), DSRTerminalElement.Text(split[1])))
        return docSystem.search(docSearch)
    }

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
