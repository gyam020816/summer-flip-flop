package eu.ha3.x.sff.connector.vertx

import eu.ha3.x.sff.api.IDocStorage
import io.vertx.core.Future
import io.vertx.rxjava.core.AbstractVerticle

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author gyam
 */
class DocStorageVerticleBinder(private val delegate: IDocStorage) : AbstractVerticle() {
    override fun start(fut: Future<Void>) {
        Binder(DEvent.LIST_DOCS.address(), NoMessage::class.java, DocListResponse::class.java).ofSingle {
            delegate.listAll().map(::DocListResponse)

        }.bind(vertx.eventBus())

        fut.complete()
    }
}

