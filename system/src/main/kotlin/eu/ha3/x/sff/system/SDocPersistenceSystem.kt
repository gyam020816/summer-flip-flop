package eu.ha3.x.sff.system

import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author Ha3
 */
interface SDocPersistenceSystem {
    suspend fun listAll(): DocListResponse
    suspend fun appendToDocs(doc: Doc): NoMessage
}