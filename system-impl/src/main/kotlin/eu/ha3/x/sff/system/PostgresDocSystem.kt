package eu.ha3.x.sff.system

import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
import io.reactivex.Single

/**
 * (Default template)
 * Created on 2019-01-05
 *
 * @author Ha3
 */
class PostgresDocSystem : IDocSystem {
    override fun listAll(): Single<DocListResponse> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun appendToDocs(doc: Doc): Single<NoMessage> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}