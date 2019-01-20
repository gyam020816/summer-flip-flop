package eu.ha3.x.sff.deployable

import eu.ha3.x.sff.deployable.SwitchableFeature.POSTGRES

/**
 * (Default template)
 * Created on 2019-01-12
 *
 * @author Ha3
 */
fun main(args: Array<String>) {
    SwitchableDeployer(setOf(POSTGRES)).run()
}
