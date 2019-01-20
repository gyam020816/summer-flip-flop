
package eu.ha3.x.sff.connector.vertx

enum class DEvent {
    LIST_DOCS,
    SYSTEM_LIST_DOCS,
    APPEND_TO_DOCS,
    SYSTEM_APPEND_TO_DOCS,
    SEARCH_DOCS,
    ;

    fun address() = name
}