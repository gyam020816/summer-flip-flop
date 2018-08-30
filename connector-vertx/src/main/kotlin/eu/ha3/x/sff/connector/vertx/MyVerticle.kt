package eu.ha3.x.sff.connector.vertx

/**
 * (Default template)
 * Created on 2018-08-30
 *
 * @author Ha3
 */


import eu.ha3.x.sff.core.Greeting
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

class MyVerticle : AbstractVerticle() {
    override fun start(fut: Future<Void>) {
        val router: Router = Router.router(vertx)
        router.route(HttpMethod.GET, "/greeting") .handler(::greeting)

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

    private fun greeting(rc: RoutingContext) {
        val query = rc.request().params()

        val name = query["name"] ?: "World"
        val reply = Greeting(0L, "Hello, $name")

        rc.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(reply))
    }
}