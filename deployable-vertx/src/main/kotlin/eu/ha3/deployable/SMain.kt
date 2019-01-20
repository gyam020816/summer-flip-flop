package eu.ha3.deployable
import eu.ha3.x.sff.api.SDocStorage
import eu.ha3.x.sff.api.SuspendedDocStorage
import eu.ha3.x.sff.connector.vertx.DJsonObject
import eu.ha3.x.sff.connector.vertx.DJsonObjectMessageCodec
import eu.ha3.x.sff.connector.vertx.SuspendedWebVerticle
import eu.ha3.x.sff.connector.vertx.coroutine.SDocStorageVertx
import eu.ha3.x.sff.connector.vertx.coroutine.SDocSystemVertx
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
import eu.ha3.x.sff.system.SDocSystem
import eu.ha3.x.sff.system.postgres.DbConnectionParams
import eu.ha3.x.sff.system.postgres.PostgresLiquibaseUpgrade
import eu.ha3.x.sff.system.postgres.PostgresSuspendedDocSystem
import eu.ha3.x.sff.system.postgres.UpgradeParams
import io.vertx.core.Vertx
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2018-10-06
 *
 * @author Ha3
 */
enum class SwitchableFeature {
    COMPONENTS_AS_SEPARATE_VERTICLES,
    POSTGRES
}

class SwitchableDeployer(private val features: Set<SwitchableFeature>): Runnable {
    override fun run() {
        val vertx = Vertx.vertx()
        vertx.eventBus().registerDefaultCodec(DJsonObject::class.java, DJsonObjectMessageCodec())

        val docStorageFn: (SDocSystem) -> SDocStorage = { docSystem: SDocSystem -> SuspendedDocStorage(docSystem) }
        val concreteDocSystem = resolveDocSystem(features)

        if (SwitchableFeature.COMPONENTS_AS_SEPARATE_VERTICLES in features) {
            val system = SDocSystemVertx()
            val storage = SDocStorageVertx()

            val senderDocStorage = storage.QuestionSender(vertx)
            val senderDocSystem = system.QuestionSender(vertx)

            val verticles = listOf(
                    SuspendedWebVerticle(senderDocStorage),
                    storage.Verticle(docStorageFn(senderDocSystem)),
                    system.Verticle(concreteDocSystem)
            )
            verticles.forEach(vertx::deployVerticle)

        } else {
            val verticles = listOf(SuspendedWebVerticle(docStorageFn(concreteDocSystem)))
            verticles.forEach(vertx::deployVerticle)
        }
    }

    private fun resolveDocSystem(features: Set<SwitchableFeature>): SDocSystem {
        return if (SwitchableFeature.POSTGRES in features) {
            val db = DbConnectionParams(
                    jdbcUrl = envOrElse("DB_URL", "jdbc:postgresql://localhost:16099/summer"),
                    user = envOrElse("DB_USER", "postgres"),
                    pass = envOrElse("DB_PASSWORD", "test123")
            )

            PostgresLiquibaseUpgrade(db, UpgradeParams("changelog.xml", "public"))
                    .upgradeDatabase()

            PostgresSuspendedDocSystem(db)

        } else {
            NoDocSystem
        }
    }

    private object NoDocSystem : SDocSystem {
        override suspend fun appendToDocs(doc: Doc) = NoMessage
        override suspend fun listAll() = DocListResponse(listOf(Doc("hello", ZonedDateTime.now())))
    }
}