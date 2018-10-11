import eu.ha3.x.sff.api.DocStorage
import eu.ha3.x.sff.api.IDocSystem
import eu.ha3.x.sff.connector.vertx.DocStorageVerticle
import eu.ha3.x.sff.connector.vertx.DocSystemVerticle
import eu.ha3.x.sff.connector.vertx.VersDocSystem
import eu.ha3.x.sff.connector.vertx.WebVerticle
import eu.ha3.x.sff.core.Doc
import io.reactivex.Single
import io.vertx.core.Vertx
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author gyam
 */
fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    val verticles = listOf(
            WebVerticle(),
            DocStorageVerticle(DocStorage(VersDocSystem())),
            DocSystemVerticle(object : IDocSystem {
                override fun listAll(): Single<List<Doc>> {
                    return Single.just(listOf(Doc("hello", ZonedDateTime.now())))
                }
            })
    )
    verticles.forEach {
        vertx.deployVerticle(it)
    }
}