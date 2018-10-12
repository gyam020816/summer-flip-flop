package eu.ha3.x.sff.api

import eu.ha3.x.sff.core.Doc
import io.reactivex.Single

/**
 * (Default template)
 * Created on 2018-10-12
 *
 * @author Ha3
 */

object Neigh
sealed class DocStorageBinder {
    data class ListAllDocsAnswer(val data: List<Doc>)
    interface ListAllDocs : (Neigh) -> Single<ListAllDocsAnswer>
}
