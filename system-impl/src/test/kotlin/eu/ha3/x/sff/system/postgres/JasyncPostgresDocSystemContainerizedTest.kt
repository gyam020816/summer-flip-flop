package eu.ha3.x.sff.system.postgres

import eu.ha3.x.sff.system.SDocPersistenceSystemTestFacade
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * (Default template)
 * Created on 2019-01-23
 *
 * @author Ha3
 */
@Testcontainers
class JasyncPostgresDocSystemContainerizedTest : SDocPersistenceSystemTestFacade<JasyncPostgresDocPersistenceSystem> {
    @Container
    private val pgContainer = KPostgreSQLContainer.create()
    private val db by lazy {
        DbConnectionParams(
                jdbcUrl = pgContainer.jdbcUrl,
                user = KPostgreSQLContainer.POSTGRES_JUNIT_USERNAME,
                pass = KPostgreSQLContainer.POSTGRES_JUNIT_PASSWORD
        )
    }

    private val SUT by lazy { JasyncPostgresDocPersistenceSystem(db) }

    override fun SUT(): JasyncPostgresDocPersistenceSystem = SUT

    @BeforeEach
    internal fun setUp() {
        assertThat(pgContainer.jdbcUrl).startsWith("jdbc:postgresql://")
        PostgresLiquibaseUpgrade(db, UpgradeParams("changelog.xml", "public"))
                .upgradeDatabase()
    }
}