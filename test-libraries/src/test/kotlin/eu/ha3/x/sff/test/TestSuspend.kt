package eu.ha3.x.sff.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

/**
 * (Default template)
 * Created on 2019-01-15
 *
 * @author Ha3
 */
fun testBlocking(coroutineFn: suspend CoroutineScope.() -> Unit): Unit = runBlocking {
    coroutineFn()

    Unit
}
