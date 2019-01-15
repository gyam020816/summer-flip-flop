package eu.ha3.x.sff.api

import eu.ha3.x.sff.system.SDocSystem
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author gyam
 */
class ReactiveDocStorage(private val docSystem: SDocSystem, private val currentTimeFn: () -> ZonedDateTime = ZonedDateTime::now)
    : RxDocStorage by ReactiveToSuspendedDocStorage(SuspendedDocStorage(docSystem, currentTimeFn))