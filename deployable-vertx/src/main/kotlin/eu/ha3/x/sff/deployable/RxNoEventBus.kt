package eu.ha3.x.sff.deployable

import eu.ha3.x.sff.deployable.SwitchableFeature.CONNECTOR_VERTX_REACTIVE
import eu.ha3.x.sff.deployable.SwitchableFeature.POSTGRES_JASYNC

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author Ha3
 */
fun main(args: Array<String>) {
    SwitchableDeployer(setOf(CONNECTOR_VERTX_REACTIVE, POSTGRES_JASYNC)).run()
}
