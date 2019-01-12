package eu.ha3.deployable
import eu.ha3.x.sff.api.DocStorage
import eu.ha3.x.sff.connector.vertx.DJsonObject
import eu.ha3.x.sff.connector.vertx.DJsonObjectMessageCodec
import eu.ha3.x.sff.connector.vertx.WebVerticle
import eu.ha3.x.sff.system.postgres.DbConnectionParams
import eu.ha3.x.sff.system.postgres.PostgresDocSystem
import eu.ha3.x.sff.system.postgres.PostgresLiquibaseUpgrade
import eu.ha3.x.sff.system.postgres.UpgradeParams
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

    val verticles = listOf(WebVerticle(DocStorage(PostgresDocSystem(db))))
    verticles.forEach(vertx::deployVerticle)
}

fun envOrElse(envName: String, defaultValue: String): String {
    val result: String? = System.getenv(envName)
    if (result is String) {
        if (result.trim() != result) {
            throw IllegalArgumentException("Environment variable $envName must not have leading or trailing whitespace: `$result` (is ${result.length} chars)")
        }

        return result;

    } else {
        println(("Missing environment variable: $envName"))
        return defaultValue
    }
}
