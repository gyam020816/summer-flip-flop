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
class DocStorage(private val docSystem: IDocSystem, private val currentTimeFn: () -> ZonedDateTime = ZonedDateTime::now) : IDocStorage {
    override fun listAll(): Single<DocListResponse> {
        return docSystem.listAll()
    }

    override fun appendToDocs(request: DocCreateRequest): Single<Doc> {
        val doc = Doc(request.name, now())
        return docSystem.appendToDocs(doc)
                .map { doc }
    }

    private fun now() = currentTimeFn()
}