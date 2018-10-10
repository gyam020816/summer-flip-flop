package eu.ha3.x.sff.api

import eu.ha3.x.sff.core.Doc
import io.reactivex.Single

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author gyam
 */
class DocStorage(private val docSystem: IDocSystem) : IDocStorage {
    override fun listAll(): Single<List<Doc>> {
        return docSystem.listAll()
    }
}