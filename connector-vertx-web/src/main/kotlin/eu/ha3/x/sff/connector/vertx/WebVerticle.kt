package eu.ha3.x.sff.connector.vertx

/**
 * (Default template)
 * Created on 2018-08-30
 *
 * @author Ha3
 */


import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.vertx.core.Future
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.Json
import io.vertx.rxjava.core.AbstractVerticle
import io.vertx.rxjava.ext.web.Router
import io.vertx.rxjava.ext.web.RoutingContext
import io.vertx.rxjava.ext.web.handler.BodyHandler

class WebVerticle : AbstractVerticle() {
    override fun start(fut: Future<Void>) {
        val router: Router = Router.router(vertx)
        router.route().handler(BodyHandler.create());
        router.route(HttpMethod.GET, "/docs").handler(::getDocs)
        router.route(HttpMethod.POST, "/docs").handler(::appendToDocs);

        Json.mapper.apply {
            registerKotlinModule()
        }

        Json.prettyMapper.apply {
            registerKotlinModule()
        }

        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(8080) { result ->
                    if (result.succeeded()) {
                        fut.complete()
                    } else {
                        fut.fail(result.cause())
                    }
                }
    }

    private fun getDocs(rc: RoutingContext) {
        vertx.eventBus().send<DJsonObject>(DEvent.LIST_DOCS.address(), NoMessage().jsonify()) { res ->
            if (res.succeeded()) {
                rc.replyJson(res.result().body().dejsonify(DocListResponse::class.java).data, 200)

            } else {
                rc.serverError()
            }
        }
    }

    private fun appendToDocs(rc: RoutingContext) {
        val parsed = rc.bodyAsString.dejsonifyByParsing(DocCreateRequest::class.java)
        vertx.eventBus().dsSend<DocResponse>(DEvent.APPEND_TO_DOCS.address(), parsed).subscribe({ res ->
            rc.replyJson(res.answer.data, 201)

        }, { err ->
            if (err is MissingKotlinParameterException) {
                rc.fail(400)

            } else {
                rc.serverError()
            }
        })
    }

    private fun RoutingContext.replyJson(obj: Any, code: Int) {
        response().setStatusCode(code)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(obj.jsonifyToPrettyString())
    }

    private fun RoutingContext.serverError() {
        response().setStatusCode(500).end()
    }
}