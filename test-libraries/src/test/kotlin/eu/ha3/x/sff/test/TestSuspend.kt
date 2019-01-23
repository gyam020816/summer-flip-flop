package eu.ha3.x.sff.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

/**
 * (Default template)
 * Created on 2019-01-15
 *
 * @author Ha3
 */

fun testBlocking(suspendedFn: suspend CoroutineScope.() -> Unit): Unit = testWithinSeconds(5, suspendedFn)

fun testWithinSeconds(seconds: Int, suspendedFn: suspend CoroutineScope.() -> Unit): Unit = runBlocking {
    withTimeout(seconds * 1000L) {
        suspendedFn()
    }

    Unit
}
