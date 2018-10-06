import eu.ha3.x.sff.connector.vertx.WebVerticle
import io.vertx.core.Vertx

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author gyam
 */
fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(WebVerticle())
}