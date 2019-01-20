package eu.ha3.x.sff.test

import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2018-10-07
 *
 * @author Ha3
 */
class TestSample {
    companion object {
        val zonedDateTimeSerialized = "2018-10-07T16:51:56.845Z"
        val zonedDateTime: ZonedDateTime = ZonedDateTime.parse(zonedDateTimeSerialized).withZoneSameInstant(ZoneOffset.UTC)
    }
}