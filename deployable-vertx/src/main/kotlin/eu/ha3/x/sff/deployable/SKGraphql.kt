package eu.ha3.x.sff.deployable

import eu.ha3.x.sff.deployable.SwitchableFeature.CONNECTOR_KGRAPHQL
import eu.ha3.x.sff.deployable.SwitchableFeature.POSTGRES_JASYNC

/**
 * (Default template)
 * Created on 2019-01-12
 *
 * @author Ha3
 */
fun main(args: Array<String>) {
    SwitchableDeployer(setOf(CONNECTOR_KGRAPHQL, POSTGRES_JASYNC)).run()
}
