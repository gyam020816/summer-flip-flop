package eu.ha3.x.sff.deployable

import eu.ha3.x.sff.deployable.SwitchableFeature.POSTGRES
import eu.ha3.x.sff.deployable.SwitchableFeature.SPRING

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author Ha3
 */
fun main(args: Array<String>) {
    SwitchableDeployer(setOf(SPRING, POSTGRES)).run()
}
