package eu.ha3.x.sff.core

import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author Ha3
 */
data class Doc(val name: String, val createdAt: ZonedDateTime)

data class DocCreateRequest(val name: String)
data class DocSearch(val query: DSROperator)

data class DocListResponse(val data: List<Doc>)

typealias NoMessage = Unit