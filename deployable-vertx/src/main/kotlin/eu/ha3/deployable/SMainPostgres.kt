package eu.ha3.deployable

import eu.ha3.deployable.SwitchableFeature.COMPONENTS_AS_SEPARATE_VERTICLES
import eu.ha3.deployable.SwitchableFeature.POSTGRES

/**
 * (Default template)
 * Created on 2019-01-12
 *
 * @author Ha3
 */
fun main(args: Array<String>) {
    SwitchableDeployer(setOf(COMPONENTS_AS_SEPARATE_VERTICLES, POSTGRES)).run()
}
