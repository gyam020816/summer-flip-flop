package eu.ha3.x.sff.api

import eu.ha3.x.sff.system.SDocPersistenceSystem
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author Ha3
 */
class ReactiveDocStorage(private val docSystem: SDocPersistenceSystem, private val currentTimeFn: () -> ZonedDateTime = ZonedDateTime::now)
    : RxDocStorage by ReactiveToCoroutineDocStorage(CoroutineDocStorage(docSystem, currentTimeFn))