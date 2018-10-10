package eu.ha3.x.sff.connector.vertx

import eu.ha3.x.sff.core.Doc

interface IResponse

data class DocListResponse(val data: List<Doc>) : IResponse
data class SystemDocListResponse(val data: List<Doc>) : IResponse
