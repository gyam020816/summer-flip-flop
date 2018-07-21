package eu.ha3.x.sff.api

import eu.ha3.x.sff.core.Payload

interface IPayloadStorage {
    fun getAll(): List<Payload>
}