package eu.ha3.x.sff.system

import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
import eu.ha3.x.sff.core.PaginatedPersistence

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author Ha3
 */
interface SDocPersistenceSystem {
   suspend fun listAll(): DocListResponse
   suspend fun appendToDocs(doc: Doc): NoMessage
   suspend fun listPaginated(paginatedPersistence: PaginatedPersistence): DocListResponse {
      TODO()
   }
}