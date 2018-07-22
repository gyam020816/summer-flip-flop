package eu.ha3.x.sff.api

import eu.ha3.x.sff.core.Payload

/**
 * (Default template)
 * Created on 2018-07-22
 *
 * @author Ha3
 */
class PayloadStorage : IPayloadStorage {
    override fun getAll(): List<Payload> = emptyList()
}