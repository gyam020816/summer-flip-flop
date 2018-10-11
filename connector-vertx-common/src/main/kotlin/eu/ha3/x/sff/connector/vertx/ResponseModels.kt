package eu.ha3.x.sff.connector.vertx

import eu.ha3.x.sff.core.Doc

interface IResponse<T>;

data class DocListResponse(val data: List<Doc>) : IResponse<List<Doc>>
data class DocResponse(val data: Doc) : IResponse<Doc>
data class SystemDocListResponse(val data: List<Doc>) : IResponse<List<Doc>>
