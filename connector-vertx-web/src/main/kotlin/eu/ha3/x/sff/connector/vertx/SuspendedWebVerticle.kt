package eu.ha3.x.sff.connector.vertx

/**
 * (Default template)
 * Created on 2018-08-30
 *
 * @author Ha3
 */
import com.fasterxml.jackson.databind.ObjectMapper
import eu.ha3.x.sff.api.SDocStorage
import eu.ha3.x.sff.api.ledger.Ledger
import eu.ha3.x.sff.api.ledger.SLedger
import eu.ha3.x.sff.api.ledger.SLedgerEvent
import eu.ha3.x.sff.core.DocCreateRequest
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.launch

object PlaceholderSLedger : SLedger {
    override suspend fun <E : SLedgerEvent> execute(command: Ledger<E>): E {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class SuspendedWebVerticle(
        private val docStorage: SDocStorage,
        private val ledger: SLedger,
        private val webObjectMapper: ObjectMapper
) : CoroutineVerticle() {
    constructor(docStorage: SDocStorage, webObjectMapper: ObjectMapper) : this(docStorage, PlaceholderSLedger, webObjectMapper)

    override suspend fun start() {
        val router: Router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.get("/docs").coroutineHandler(::getDocs)
        router.post("/docs").coroutineHandler(::appendToDocs)
        router.post("/ledgers/accounts").coroutineHandler(::openAccount)

        vertx
                .createHttpServer()
                .requestHandler(router)
                .listenAwait(8080)
    }

    private suspend fun getDocs(rc: RoutingContext) {
        try {
            val result = docStorage.listAll()
            rc.replyJson(result.data, 200)

        } catch (e: Exception) {
            rc.serverError()
        }
    }

    private suspend fun appendToDocs(rc: RoutingContext) {
        val java = DocCreateRequest::class.java
        val createRequest: DocCreateRequest = try {
            webObjectMapper.readValue(rc.bodyAsString, java)

        } catch (e: com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException) {
            rc.fail(400)
            return
        }

        try {
            val result = docStorage.appendToDocs(createRequest)
            rc.replyJson(result, 201)

        } catch (e: Exception) {
            rc.serverError()
        }
    }

    private suspend fun openAccount(rc: RoutingContext) {
        try {
            val command = command<Ledger.OpenAccount>(rc)
            val event = ledger.execute(command)
            rc.replyJson(event, 201)

        } catch (e: Exception) {
            rc.serverError()
        }
    }

    class WebVerticleBadRequest(e: java.lang.Exception) : RuntimeException(e)

    private inline fun <reified T> command(rc: RoutingContext): T {
        try {
            return webObjectMapper.readValue<T>(rc.bodyAsString, T::class.java)

        } catch (e: com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException) {
            rc.fail(400)
            throw WebVerticleBadRequest(e)
        }
    }

    private fun RoutingContext.replyJson(obj: Any, code: Int) {
        response().setStatusCode(code)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(webObjectMapper.writeValueAsString(obj))
    }

    private fun RoutingContext.serverError() {
        response().setStatusCode(500).end()
    }

    private fun Route.coroutineHandler(blockFn: suspend (RoutingContext) -> Unit): Route = handler { context ->
        launch(context.vertx().dispatcher()) {
            try {
                blockFn(context)

            } catch (e: Exception) {
                context.fail(e)
            }
        }
    }
}