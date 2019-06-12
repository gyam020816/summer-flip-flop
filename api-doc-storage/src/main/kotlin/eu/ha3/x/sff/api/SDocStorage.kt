package eu.ha3.x.sff.api

import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocCreateRequest
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.Paginated

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author Ha3
 */
interface SDocStorage {
    suspend fun appendToDocs(request: DocCreateRequest): Doc
    suspend fun listAll(): DocListResponse
    suspend fun listPaginated(paginated: Paginated): DocListResponse {
        TODO()
    }
}