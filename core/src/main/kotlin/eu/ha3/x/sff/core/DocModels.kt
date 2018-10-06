package eu.ha3.x.sff.core

import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author gyam
 */
data class Doc(val name: String, val createdAt: ZonedDateTime)

data class DocListResponse(val data: List<Doc>)