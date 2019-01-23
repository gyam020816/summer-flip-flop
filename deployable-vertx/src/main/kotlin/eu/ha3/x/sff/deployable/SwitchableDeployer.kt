package eu.ha3.x.sff.deployable
import com.fasterxml.jackson.databind.SerializationFeature
import eu.ha3.x.sff.api.ReactiveDocStorage
import eu.ha3.x.sff.api.RxDocStorage
import eu.ha3.x.sff.api.SDocStorage
import eu.ha3.x.sff.api.SuspendedDocStorage
import eu.ha3.x.sff.connector.spring.Application
import eu.ha3.x.sff.connector.vertx.*
import eu.ha3.x.sff.connector.vertx.coroutine.SDocStorageVertx
import eu.ha3.x.sff.connector.vertx.coroutine.SDocSystemVertx
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
import eu.ha3.x.sff.json.KObjectMapper
import eu.ha3.x.sff.system.ReactiveToSuspendedDocSystem
import eu.ha3.x.sff.system.SDocSystem
import eu.ha3.x.sff.system.SuspendedToRxDocSystem
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
    POSTGRES,
    POSTGRES_JASYNC,
    REACTIVE,
    SPRING
}

class SwitchableDeployer(private val features: Set<SwitchableFeature>): Runnable {
    private val webObjectMapper = KObjectMapper.newInstance().apply {
        configure(SerializationFeature.INDENT_OUTPUT, true)
    }

    override fun run() {
        val concreteDocSystem = resolveDocSystem(features)

        if (SwitchableFeature.SPRING in features) {
            springReactive(concreteDocSystem)

        } else {
            if (SwitchableFeature.REACTIVE in features) {
                reactive(concreteDocSystem)

            } else {
                suspended(concreteDocSystem)
            }
        }
    }

    private fun springReactive(concreteDocSystem: SDocSystem) {
        // https://github.com/spring-projects/spring-boot/issues/8115#issuecomment-326910814
        SpringApplication(Application::class.java).apply {
            addInitializers(beans {
                bean<RxDocStorage> { ReactiveDocStorage(concreteDocSystem) }
            })
        }.run()
    }

    private fun suspended(concreteDocSystem: SDocSystem) {
        val vertx: Vertx = Vertx.vertx()
        val eventBus = vertx.eventBus()
        doRegisterCodecs(eventBus)

        val docStorageFn: (SDocSystem) -> SDocStorage = { docSystem: SDocSystem -> SuspendedDocStorage(docSystem) }

        if (SwitchableFeature.COMPONENTS_AS_SEPARATE_VERTICLES in features) {
            val system = SDocSystemVertx()
            val storage = SDocStorageVertx()

            val senderDocStorage = storage.QuestionSender(vertx)
            val senderDocSystem = system.QuestionSender(vertx)

            listOf(
                    (1..16).map { SuspendedWebVerticle(senderDocStorage, webObjectMapper) },
                    (1..16).map { storage.Verticle(docStorageFn(senderDocSystem)) },
                    (1..16).map { system.Verticle(concreteDocSystem) }
            ).flatMap { it }.forEach {
                vertx.deployVerticle(it)
                println("Deployed ${it::class.java.simpleName}.")
            }

        } else {
            val verticles = listOf(SuspendedWebVerticle(docStorageFn(concreteDocSystem), webObjectMapper))
            verticles.forEach {
                vertx.deployVerticle(it)
                println("Deployed ${it::class.java.simpleName}.")
            }
        }
    }

    private fun reactive(concreteDocSystem: SDocSystem) {
        val vertx: io.vertx.rxjava.core.Vertx = io.vertx.rxjava.core.Vertx.vertx()
        doRegisterCodecs(vertx.eventBus().delegate)

        if (SwitchableFeature.COMPONENTS_AS_SEPARATE_VERTICLES in features) {
            val system = RxDocSystemVertx()
            val storage = RxDocStorageVertx()

            val rxBus = vertx.eventBus()

            val senderDocStorage = storage.QuestionSender(rxBus)
            val senderDocSystem = system.QuestionSender(rxBus)

            val verticles = listOf(
                    ReactiveWebVerticle(senderDocStorage, webObjectMapper),
                    storage.Verticle(ReactiveDocStorage(SuspendedToRxDocSystem(senderDocSystem))),
                    system.Verticle(ReactiveToSuspendedDocSystem(concreteDocSystem))
            )
            verticles.forEach(vertx::deployVerticle)

        } else {
            val verticles = listOf(ReactiveWebVerticle(ReactiveDocStorage(concreteDocSystem), webObjectMapper))
            verticles.forEach(vertx::deployVerticle)
        }
    }

    private fun resolveDocSystem(features: Set<SwitchableFeature>): SDocSystem {
        return if (SwitchableFeature.POSTGRES in features || SwitchableFeature.POSTGRES_JASYNC in features) {
            val db = DbConnectionParams(
                    jdbcUrl = envOrElse("DB_URL", "jdbc:postgresql://localhost:16099/summer"),
                    user = envOrElse("DB_USER", "postgres"),
                    pass = envOrElse("DB_PASSWORD", "test123")
            )

            PostgresLiquibaseUpgrade(db, UpgradeParams("changelog.xml", "public"))
                    .upgradeDatabase()

            if (SwitchableFeature.POSTGRES_JASYNC in features) {
                JdbcPostgresSuspendedDocSystem(db)
            } else {
                JasyncPostgresSuspendedDocSystem(db)
            }

        } else {
            NoDocSystem
        }
    }

    private fun doRegisterCodecs(eventBus: EventBus) {
        eventBus.registerDefaultCodec(DJsonObject::class.java, DJsonObjectMessageCodec())
    }

    private object NoDocSystem : SDocSystem {
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