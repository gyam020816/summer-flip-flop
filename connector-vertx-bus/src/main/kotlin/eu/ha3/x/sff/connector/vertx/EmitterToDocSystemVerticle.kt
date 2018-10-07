package eu.ha3.x.sff.connector.vertx

import eu.ha3.x.sff.api.IDocSystem
import eu.ha3.x.sff.core.Doc
import io.reactivex.Single
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject

/**
 * (Default template)
 * Created on 2018-10-07
 *
 * @author Ha3
 */
class EmitterToDocSystemVerticle : AbstractVerticle(), IDocSystem {
    override fun listAll(): Single<List<Doc>> {
        return Single.create<List<Doc>> { handler ->
            vertx.eventBus().send<JsonObject>(DEvent.SYSTEM_LIST_DOCS.address(), JsonObject()) { res ->
                if (res.succeeded()) {
                    handler.onSuccess(res.result().body().mapTo(SystemDocListResponse::class.java).data)

                } else {
                    handler.onError(res.cause())
                }
            }
        }
    }
}
