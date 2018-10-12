
import eu.ha3.x.sff.api.DocStorage
import eu.ha3.x.sff.connector.vertx.DJsonObject
import eu.ha3.x.sff.connector.vertx.DJsonObjectMessageCodec
import eu.ha3.x.sff.connector.vertx.DocStorageVerticle
import eu.ha3.x.sff.connector.vertx.WebVerticle
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.system.IDocSystem
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
    vertx.eventBus().registerDefaultCodec(DJsonObject::class.java, DJsonObjectMessageCodec())

    val verticles = listOf(
            WebVerticle(),
            DocStorageVerticle(DocStorage(object : IDocSystem {
                override fun listAll(): Single<List<Doc>> = Single.just(listOf(Doc("hello", ZonedDateTime.now())))
            }))
    )
    verticles.forEach(vertx::deployVerticle)
}