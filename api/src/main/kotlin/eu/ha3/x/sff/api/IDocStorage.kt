package eu.ha3.x.sff.api

import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocCreateRequest
import eu.ha3.x.sff.core.DocListResponse
import io.reactivex.Single

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author gyam
 */
interface IDocStorage {
    fun appendToDocs(doc: DocCreateRequest): Single<Doc>
    fun listAll(): Single<DocListResponse>
}