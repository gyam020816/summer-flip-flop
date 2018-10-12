
import eu.ha3.x.sff.api.DocStorage
import eu.ha3.x.sff.connector.vertx.*
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
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
            WebVerticle(DocStorageVerticle.VersDocStorage),
            DocStorageVerticle(DocStorage(DocSystemVerticle.VersDocStorage)),
            DocSystemVerticle(object : IDocSystem {
                override fun appendToDocs(doc: Doc) = Single.just(NoMessage())
                override fun listAll() = Single.just(DocListResponse(listOf(Doc("hello", ZonedDateTime.now()))))
            })
    )
    verticles.forEach(vertx::deployVerticle)
}