package eu.ha3.x.sff.core

/**
 * (Default template)
 * Created on 2019-01-12
 *
 * @author Ha3
 */
class ConnectorException : RuntimeException {
    constructor(message: String, ex: Exception?): super(message, ex) {}
    constructor(message: String): super(message) {}
    constructor(ex: Exception): super(ex) {}
}