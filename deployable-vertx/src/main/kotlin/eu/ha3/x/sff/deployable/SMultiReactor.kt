package eu.ha3.x.sff.deployable

import eu.ha3.x.sff.deployable.SwitchableFeature.*

/**
 * (Default template)
 * Created on 2019-01-12
 *
 * @author Ha3
 */
fun main(args: Array<String>) {
    SwitchableDeployer(setOf(CONNECTOR_VERTX_COROUTINE, COMPONENTS_AS_SEPARATE_VERTICLES, POSTGRES_JASYNC)).run()
}
