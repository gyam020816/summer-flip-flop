package eu.ha3.x.sff.test

/**
 * (Default template)
 * Created on 2018-10-07
 *
 * @author Ha3
 */
fun <T> verify(consumer: T.() -> Unit): (T) -> Boolean = { t ->
    consumer(t)
    true
}