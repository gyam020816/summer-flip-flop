package eu.ha3.deployable
import eu.ha3.x.sff.api.ReactiveToSuspendedDocStorage
import eu.ha3.x.sff.api.SuspendedDocStorage
import eu.ha3.x.sff.connector.vertx.DJsonObject
import eu.ha3.x.sff.connector.vertx.DJsonObjectMessageCodec
import eu.ha3.x.sff.connector.vertx.WebVerticle
import eu.ha3.x.sff.system.postgres.DbConnectionParams
import eu.ha3.x.sff.system.postgres.PostgresLiquibaseUpgrade
import eu.ha3.x.sff.system.postgres.PostgresSuspendedDocSystem
import eu.ha3.x.sff.system.postgres.UpgradeParams
import io.vertx.core.Verticle
import io.vertx.core.Vertx

/**
 * (Default template)
 * Created on 2019-01-12
 *
 * @author gyam
 */
fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    vertx.eventBus().registerDefaultCodec(DJsonObject::class.java, DJsonObjectMessageCodec())

    val db = DbConnectionParams(
            jdbcUrl = envOrElse("DB_URL", "jdbc:postgresql://localhost:16099/summer"),
            user = envOrElse("DB_USER", "postgres"),
            pass = envOrElse("DB_PASSWORD", "test123")
    )

    PostgresLiquibaseUpgrade(db, UpgradeParams("changelog.xml", "public"))
            .upgradeDatabase()

    //
    val verticles = listOf(webVerticleWithDependencies(db))
    verticles.forEach(vertx::deployVerticle)
}

/**
 * For more information, check out [this video](https://youtu.be/3YxaaGgTQYM?t=63).
 */
private fun webVerticleWithDependencies(db: DbConnectionParams): Verticle = WebVerticle(
        ReactiveToSuspendedDocStorage(
                SuspendedDocStorage(
                        PostgresSuspendedDocSystem(db)
                )
        )
)
