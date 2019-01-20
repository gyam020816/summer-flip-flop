package eu.ha3.deployable

import eu.ha3.deployable.SwitchableFeature.REACTIVE_LEGACY

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author Ha3
 */
fun main(args: Array<String>) {
    SwitchableDeployer(setOf(REACTIVE_LEGACY)).run()
}
