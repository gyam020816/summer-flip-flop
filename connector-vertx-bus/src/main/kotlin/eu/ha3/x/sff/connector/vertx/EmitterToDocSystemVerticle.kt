package eu.ha3.x.sff.connector.vertx

import eu.ha3.x.sff.api.IDocSystem
import eu.ha3.x.sff.core.Doc
import io.reactivex.Single
import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.AbstractVerticle

/**
 * (Default template)
 * Created on 2018-10-07
 *
 * @author Ha3
 */
class EmitterToDocSystemVerticle : AbstractVerticle(), IDocSystem {
    override fun listAll(): Single<List<Doc>> {
        return Single.create<List<Doc>> { handler ->
            vertx.eventBus().rxSend<JsonObject>(DEvent.SYSTEM_LIST_DOCS.address(), JsonObject()).subscribe({ res ->
                handler.onSuccess(res.body().dejsonify(SystemDocListResponse::class.java).data)

            }, handler::onError);
        }
    }
}
