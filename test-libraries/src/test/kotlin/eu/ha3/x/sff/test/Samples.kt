package eu.ha3.x.sff.test

import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

/**
 * (Default template)
 * Created on 2018-10-07
 *
 * @author Ha3
 */
class TestSample {
    companion object {
        fun randomUuid(): UUID = UUID.randomUUID()

        val zonedDateTimeSerialized = "2018-10-07T16:51:56.845Z"
        val zonedDateTime: ZonedDateTime = ZonedDateTime.parse(zonedDateTimeSerialized).withZoneSameInstant(ZoneOffset.UTC)
        val uuidA: UUID = UUID.fromString("11111111-78c7-4a85-a5aa-4de65f7aec95")
        val uuidB: UUID = UUID.fromString("22222222-78c7-4a85-a5aa-4de65f7aec95")
        val uuidC: UUID = UUID.fromString("33333333-78c7-4a85-a5aa-4de65f7aec95")
    }
}