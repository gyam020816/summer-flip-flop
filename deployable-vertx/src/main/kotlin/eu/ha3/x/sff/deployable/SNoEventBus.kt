package eu.ha3.x.sff.deployable

import eu.ha3.x.sff.deployable.SwitchableFeature.CONNECTOR_VERTX_COROUTINE
import eu.ha3.x.sff.deployable.SwitchableFeature.POSTGRES_JASYNC

/**
 * (Default template)
 * Created on 2019-01-12
 *
 * @author Ha3
 */
fun main(args: Array<String>) {
    SwitchableDeployer(setOf(CONNECTOR_VERTX_COROUTINE, POSTGRES_JASYNC)).run()
}
