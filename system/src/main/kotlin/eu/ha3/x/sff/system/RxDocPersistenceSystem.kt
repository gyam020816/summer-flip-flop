package eu.ha3.x.sff.system

import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
import eu.ha3.x.sff.core.PaginatedPersistence
import io.reactivex.Single

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author Ha3
 */
interface RxDocPersistenceSystem {
    fun listAll(): Single<DocListResponse>
    fun appendToDocs(doc: Doc): Single<NoMessage>
    fun listPaginated(paginatedPersistence: PaginatedPersistence): Single<DocListResponse> {
        TODO()
    }
}