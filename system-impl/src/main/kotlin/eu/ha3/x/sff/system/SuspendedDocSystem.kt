package eu.ha3.x.sff.system

import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage

/**
 * (Default template)
 * Created on 2018-12-09
 *
 * @author Ha3
 */
class SuspendedDocSystem(private val docSystem: IDocSystem) : SDocSystem {
    override suspend fun listAll(): DocListResponse =
            docSystem.listAll().blockingGet()

    override suspend fun appendToDocs(doc: Doc): NoMessage =
            docSystem.appendToDocs(doc).blockingGet()
}
