package eu.ha3.x.sff.api

import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocCreateRequest
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.system.IDocSystem
import io.reactivex.Single
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author gyam
 */
class DocStorage(private val docSystem: IDocSystem) : IDocStorage {
    override fun listAll(): Single<DocListResponse> {
        return docSystem.listAll()
    }

    override fun appendToDocs(request: DocCreateRequest): Single<Doc> {
        return Single.just(Doc(request.name, now()))
                .doOnSuccess { document -> docSystem.appendToDocs(document) }
    }

    private fun now() = ZonedDateTime.now()
}