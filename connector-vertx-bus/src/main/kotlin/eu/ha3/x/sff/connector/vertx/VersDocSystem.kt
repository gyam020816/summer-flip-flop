package eu.ha3.x.sff.connector.vertx

import eu.ha3.x.sff.api.IDocSystem
import eu.ha3.x.sff.core.Doc
import io.reactivex.Single
import io.vertx.rxjava.core.AbstractVerticle

/**
 * (Default template)
 * Created on 2018-10-07
 *
 * @author Ha3
 */
class VersDocSystem : AbstractVerticle(), IDocSystem {
    override fun listAll(): Single<List<Doc>> {
        return Single.create<List<Doc>> { handler ->
            vertx.eventBus().dsSend<SystemDocListResponse>(DEvent.SYSTEM_LIST_DOCS.address(), NoMessage()).subscribe({ res ->
                handler.onSuccess(res.answer.data)

            }, handler::onError);
        }
    }
}
