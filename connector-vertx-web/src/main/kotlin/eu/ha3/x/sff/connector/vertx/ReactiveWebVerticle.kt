package eu.ha3.x.sff.connector.vertx

/**
 * (Default template)
 * Created on 2018-08-30
 *
 * @author Ha3
 */
import com.fasterxml.jackson.databind.ObjectMapper
import eu.ha3.x.sff.api.RxDocStorage
import eu.ha3.x.sff.core.DocCreateRequest
import io.vertx.core.Future
import io.vertx.core.http.HttpMethod
import io.vertx.rxjava.core.AbstractVerticle
import io.vertx.rxjava.ext.web.Router
import io.vertx.rxjava.ext.web.RoutingContext
import io.vertx.rxjava.ext.web.handler.BodyHandler

class ReactiveWebVerticle(private val docStorage: RxDocStorage, private val webObjectMapper: ObjectMapper) : AbstractVerticle() {
    override fun start(fut: Future<Void>) {
        val router: Router = Router.router(vertx)
        router.route().handler(BodyHandler.create());
        router.route(HttpMethod.GET, "/docs").handler(::getDocs)
        router.route(HttpMethod.POST, "/docs").handler(::appendToDocs);

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
        docStorage.listAll().subscribe({ res ->
            rc.replyJson(res.data, 200)

        }, { error ->
            rc.serverError()
        })
    }

    private fun appendToDocs(rc: RoutingContext) {
        val createRequest: DocCreateRequest = try {
            webObjectMapper.readValue(rc.bodyAsString, DocCreateRequest::class.java)

        } catch (e: com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException) {
            rc.fail(400)
            return
        }

        docStorage.appendToDocs(createRequest).subscribe({ res ->
            rc.replyJson(res, 201)

        }, { err ->
            rc.serverError()
        })
    }

    private fun RoutingContext.replyJson(obj: Any, code: Int) {
        response().setStatusCode(code)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(webObjectMapper.writeValueAsString(obj))
    }

    private fun RoutingContext.serverError() {
        response().setStatusCode(500).end()
    }
}