package eu.ha3.x.sff.core

import java.time.ZonedDateTime
import java.util.*

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author Ha3
 */
data class Doc(val docId: DocId, val name: String, val createdAt: ZonedDateTime)
data class DocId(val value: UUID)

data class DocCreateRequest(val name: String)
data class DocListResponse(val data: List<Doc>)

data class DocListPaginationRequest(val first: Int)

data class PaginatedPersistence(val first: Int, val after: DocId? = null)

typealias NoMessage = Unit