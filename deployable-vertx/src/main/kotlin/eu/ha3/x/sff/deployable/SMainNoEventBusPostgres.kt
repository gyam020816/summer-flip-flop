package eu.ha3.x.sff.deployable

/**
 * (Default template)
 * Created on 2019-01-12
 *
 * @author Ha3
 */
fun main(args: Array<String>) {
    SwitchableDeployer(setOf(SwitchableFeature.POSTGRES_JASYNC)).run()
}
