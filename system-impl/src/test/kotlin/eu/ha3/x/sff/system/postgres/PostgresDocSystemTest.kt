package eu.ha3.x.sff.system.postgres

import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2019-01-05
 *
 * @author Ha3
 */
@Testcontainers
class PostgresDocSystemTest {
    @Container
    private val pgContainer = KPostgreSQLContainer.create()
    private val db by lazy {
        DbConnectionParams(
                jdbcUrl = pgContainer.jdbcUrl,
                user = KPostgreSQLContainer.POSTGRES_JUNIT_USERNAME,
                pass = KPostgreSQLContainer.POSTGRES_JUNIT_PASSWORD
        )
    }

    private val SUT by lazy { PostgresDocSystem(db) }

    @BeforeEach
    internal fun setUp() {
        assertThat(pgContainer.jdbcUrl).startsWith("jdbc:postgresql://")
        PostgresLiquibaseUpgrade(db, UpgradeParams("changelog.xml", "public"))
                .upgradeDatabase()
    }

    @Test
    fun `it should insert a document and retrieve it`() {
        val document = Doc("a", ZonedDateTime.of(2000, 12, 1, 23, 40, 50, 0, ZoneOffset.UTC))

        // Exercise
        SUT.appendToDocs(document).blockingGet()
        val result = SUT.listAll().blockingGet()

        // Verify
        assertThat(result).isEqualTo(DocListResponse(listOf(document)))
    }
}

class KPostgreSQLContainer(imageName: String) : PostgreSQLContainer<KPostgreSQLContainer>(imageName) {
    // See https://github.com/testcontainers/testcontainers-java/issues/318#issuecomment-290692749

    companion object {
        internal const val POSTGRES_JUNIT_USERNAME = "test_junit_username"
        internal const val POSTGRES_JUNIT_PASSWORD = "test_junit_password"

        fun create() = KPostgreSQLContainer("postgres:9.6.2")
                .withUsername(POSTGRES_JUNIT_USERNAME)
                .withPassword(POSTGRES_JUNIT_PASSWORD)
    }
}