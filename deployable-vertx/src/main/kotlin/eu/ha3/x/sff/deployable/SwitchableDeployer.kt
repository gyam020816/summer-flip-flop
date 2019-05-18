package eu.ha3.x.sff.deployable
import com.fasterxml.jackson.databind.SerializationFeature
import eu.ha3.x.sff.api.CoroutineDocStorage
import eu.ha3.x.sff.api.ReactiveDocStorage
import eu.ha3.x.sff.api.RxDocStorage
import eu.ha3.x.sff.api.SDocStorage
import eu.ha3.x.sff.connector.kgraphql.KtorGraphqlApplication
import eu.ha3.x.sff.connector.ktor.KtorApplication
import eu.ha3.x.sff.connector.spring.Application
import eu.ha3.x.sff.connector.vertx.*
import eu.ha3.x.sff.connector.vertx.coroutine.SDocPersistenceSystemVertxBinder
import eu.ha3.x.sff.connector.vertx.coroutine.SDocStorageVertxBinder
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
import eu.ha3.x.sff.deployable.SwitchableFeature.*
import eu.ha3.x.sff.json.KObjectMapper
import eu.ha3.x.sff.system.CoroutineToReactiveDocPersistenceSystem
import eu.ha3.x.sff.system.ReactiveToCoroutineDocPersistenceSystem
import eu.ha3.x.sff.system.SDocPersistenceSystem
import eu.ha3.x.sff.system.postgres.*
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import org.springframework.boot.SpringApplication
import org.springframework.context.support.beans
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author Ha3
 */
enum class SwitchableFeature {
    COMPONENTS_AS_SEPARATE_VERTICLES,
    POSTGRES_JDBC,
    POSTGRES_JASYNC,
    CONNECTOR_VERTX_COROUTINE,
    CONNECTOR_VERTX_REACTIVE,
    CONNECTOR_SPRING,
    CONNECTOR_KTOR,
    CONNECTOR_KGRAPHQL
}

class SwitchableDeployer(private val features: Set<SwitchableFeature>): Runnable {
    private val webObjectMapper = KObjectMapper.newInstance().apply {
        configure(SerializationFeature.INDENT_OUTPUT, true)
    }

    override fun run() {
        val concreteDocSystem = resolveDocSystem(features)

        when {
            CONNECTOR_VERTX_COROUTINE in features -> vertxCoroutine(concreteDocSystem)
            CONNECTOR_VERTX_REACTIVE in features -> vertxReactive(concreteDocSystem)
            CONNECTOR_SPRING in features -> springReactive(concreteDocSystem)
            CONNECTOR_KTOR in features -> ktorCoroutine(concreteDocSystem)
            CONNECTOR_KGRAPHQL in features -> kGraphqlCoroutine(concreteDocSystem)
            else -> throw IllegalArgumentException("Missing connector feature")
        }
    }

    private fun springReactive(concreteDocSystem: SDocPersistenceSystem) {
        // https://github.com/spring-projects/spring-boot/issues/8115#issuecomment-326910814
        SpringApplication(Application::class.java).apply {
            addInitializers(beans {
                bean<RxDocStorage> { ReactiveDocStorage(concreteDocSystem) }
            })
        }.run()
    }

    private fun ktorCoroutine(concreteDocSystem: SDocPersistenceSystem) {
        KtorApplication.newEmbedded(CoroutineDocStorage(concreteDocSystem), webObjectMapper).start(wait = true)
    }

    private fun kGraphqlCoroutine(concreteDocSystem: SDocPersistenceSystem) {
        KtorGraphqlApplication.newEmbedded(CoroutineDocStorage(concreteDocSystem), webObjectMapper).start(wait = true)
    }

    private fun vertxCoroutine(concreteDocSystem: SDocPersistenceSystem) {
        val vertx: Vertx = Vertx.vertx()
        val eventBus = vertx.eventBus()
        doRegisterCodecs(eventBus)

        val docStorageFn: (SDocPersistenceSystem) -> SDocStorage = { docSystem: SDocPersistenceSystem -> CoroutineDocStorage(docSystem) }

        if (COMPONENTS_AS_SEPARATE_VERTICLES in features) {
            val system = SDocPersistenceSystemVertxBinder()
            val storage = SDocStorageVertxBinder()

            val senderDocStorage = storage.QuestionSender(vertx)
            val senderDocSystem = system.QuestionSender(vertx)

            listOf(
                    (1..16).map { CoroutineWebVerticle(senderDocStorage, webObjectMapper) },
                    (1..16).map { storage.Verticle(docStorageFn(senderDocSystem)) },
                    (1..16).map { system.Verticle(concreteDocSystem) }
            ).flatMap { it }.forEach {
                vertx.deployVerticle(it)
                println("Deployed ${it::class.java.simpleName}.")
            }

        } else {
            val verticles = listOf(CoroutineWebVerticle(docStorageFn(concreteDocSystem), webObjectMapper))
            verticles.forEach {
                vertx.deployVerticle(it)
                println("Deployed ${it::class.java.simpleName}.")
            }
        }
    }

    private fun vertxReactive(concreteDocSystem: SDocPersistenceSystem) {
        val vertx: io.vertx.rxjava.core.Vertx = io.vertx.rxjava.core.Vertx.vertx()
        doRegisterCodecs(vertx.eventBus().delegate)

        if (COMPONENTS_AS_SEPARATE_VERTICLES in features) {
            val system = RxDocSystemVertx()
            val storage = RxDocStorageVertx()

            val rxBus = vertx.eventBus()

            val senderDocStorage = storage.QuestionSender(rxBus)
            val senderDocSystem = system.QuestionSender(rxBus)

            val verticles = listOf(
                    ReactiveWebVerticle(senderDocStorage, webObjectMapper),
                    storage.Verticle(ReactiveDocStorage(CoroutineToReactiveDocPersistenceSystem(senderDocSystem))),
                    system.Verticle(ReactiveToCoroutineDocPersistenceSystem(concreteDocSystem))
            )
            verticles.forEach(vertx::deployVerticle)

        } else {
            val verticles = listOf(ReactiveWebVerticle(ReactiveDocStorage(concreteDocSystem), webObjectMapper))
            verticles.forEach(vertx::deployVerticle)
        }
    }

    private fun resolveDocSystem(features: Set<SwitchableFeature>): SDocPersistenceSystem {
        return if (POSTGRES_JDBC in features || POSTGRES_JASYNC in features) {
            val db = DbConnectionParams(
                    jdbcUrl = envOrElse("DB_URL", "jdbc:postgresql://localhost:16099/summer"),
                    user = envOrElse("DB_USER", "postgres"),
                    pass = envOrElse("DB_PASSWORD", "test123")
            )

            PostgresLiquibaseUpgrade(db, UpgradeParams("changelog.xml", "public"))
                    .upgradeDatabase()

            if (POSTGRES_JASYNC in features) {
                JdbcPostgresDocPersistenceSystem(db)
            } else {
                JasyncPostgresDocPersistenceSystem(db)
            }

        } else {
            NoDocPersistenceSystem
        }
    }

    private fun doRegisterCodecs(eventBus: EventBus) {
        eventBus.registerDefaultCodec(DJsonObject::class.java, DJsonObjectMessageCodec())
    }

    private object NoDocPersistenceSystem : SDocPersistenceSystem {
        override suspend fun appendToDocs(doc: Doc) = NoMessage
        override suspend fun listAll() = DocListResponse(listOf(Doc("hello", ZonedDateTime.now())))
    }

    private fun envOrElse(envName: String, defaultValue: String): String {
        val result: String? = System.getenv(envName)
        if (result is String) {
            if (result.trim() != result) {
                throw IllegalArgumentException("Environment variable $envName must not have leading or trailing whitespace: `$result` (is ${result.length} chars)")
            }

            return result

        } else {
            println(("Missing environment variable: $envName"))
            return defaultValue
        }
    }
}