package eu.ha3.x.sff.deployable

import eu.ha3.x.sff.deployable.SwitchableFeature.COMPONENTS_AS_SEPARATE_VERTICLES
import eu.ha3.x.sff.deployable.SwitchableFeature.REACTIVE_LEGACY

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author Ha3
 */
fun main(args: Array<String>) {
    SwitchableDeployer(setOf(REACTIVE_LEGACY, COMPONENTS_AS_SEPARATE_VERTICLES)).run()
}
