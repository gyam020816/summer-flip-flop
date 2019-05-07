package eu.ha3.x.sff.system.postgres

import eu.ha3.x.sff.system.SDocSystemTestFacade
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * (Default template)
 * Created on 2019-05-05
 *
 * @author Ha3
 */
@Testcontainers
internal class R2dbcPostgreSuspendedDocSystemTest : SDocSystemTestFacade<R2dbcPostgreSuspendedDocSystem> {
    @Container
    private val pgContainer = KPostgreSQLContainer.create()
    private val db by lazy {
        DbConnectionParams(
                jdbcUrl = pgContainer.jdbcUrl,
                user = KPostgreSQLContainer.POSTGRES_JUNIT_USERNAME,
                pass = KPostgreSQLContainer.POSTGRES_JUNIT_PASSWORD
        )
    }

    private val SUT by lazy { R2dbcPostgreSuspendedDocSystem(db) }

    override fun SUT(): R2dbcPostgreSuspendedDocSystem = SUT

    @BeforeEach
    internal fun setUp() {
        Assertions.assertThat(pgContainer.jdbcUrl).startsWith("jdbc:postgresql://")
        PostgresLiquibaseUpgrade(db, UpgradeParams("changelog.xml", "public"))
                .upgradeDatabase()
    }
}