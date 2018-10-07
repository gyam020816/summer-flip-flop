package eu.ha3.x.sff.connector.vertx

/**
 * (Default template)
 * Created on 2018-08-30
 *
 * @author Ha3
 */


import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import eu.ha3.x.sff.core.Greeting
import eu.ha3.x.sff.core.SearchQuery
import eu.ha3.x.sff.core.SearchResult
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

class WebVerticle : AbstractVerticle() {
    override fun start(fut: Future<Void>) {
        val router: Router = Router.router(vertx)
        router.route(HttpMethod.GET, "/greeting").handler(::greeting)
        router.route(HttpMethod.GET, "/search").handler(::search)
        router.route(HttpMethod.GET, "/docs").handler(::docs)

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

    private fun greeting(rc: RoutingContext) {
        val query = rc.request().params()

        val name = query["name"] ?: "World"
        val reply = Greeting(0L, "Hello, $name")

        vertx.eventBus().send<String>(
                VEvent.GREETING.toString(),
                VOp.GREETING.toString()
        ) { res ->
            if (res.succeeded()) {
                rc.replyJson(reply, 200)

            } else {
                rc.serverError()
            }
        }
    }

    private fun search(rc: RoutingContext) {
        val query = rc.request().params()

        val searchQuery = query["query"] ?: ""

        vertx.eventBus().send<SearchResult>(VEvent.SEARCH.toString(), JsonObject.mapFrom(SearchQuery(searchQuery))) { res ->
            if (res.succeeded()) {
                rc.replyJson(listOf(res.result().body()), 200)

            } else {
                rc.serverError()
            }
        }
    }

    private fun docs(rc: RoutingContext) {
        vertx.eventBus().send<DocListResponse>(DEvent.LIST_DOCS.address(), NoMessage()) { res ->
            if (res.succeeded()) {
                rc.replyJson(res.result().body().data, 200)

            } else {
                rc.serverError()
            }
        }
    }

    fun RoutingContext.replyJson(obj: Any, code: Int) {
        response().setStatusCode(code)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(obj))
    }

    fun RoutingContext.serverError() {
        response().setStatusCode(500).end()
    }
}