package eu.ha3.x.sff.system

import java.util.*

interface IUuidProvider {
    fun newUuid(): UUID
}