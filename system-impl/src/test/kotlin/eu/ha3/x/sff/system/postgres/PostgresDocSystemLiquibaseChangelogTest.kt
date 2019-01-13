package eu.ha3.x.sff.system.postgres

import eu.ha3.x.sff.system.postgres.DbConnection.Companion.open
import eu.ha3.x.sff.test.TestSample
import eu.ha3.x.sff.test.assertFail
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * (Default template)
 * Created on 2019-01-13
 *
 * @author Ha3
 */
@Testcontainers
class PostgresDocSystemLiquibaseChangelogTest {
    @Container
    private val pgContainer = KPostgreSQLContainer.create()
    private val db by lazy {
        DbConnectionParams(
                jdbcUrl = pgContainer.jdbcUrl,
                user = KPostgreSQLContainer.POSTGRES_JUNIT_USERNAME,
                pass = KPostgreSQLContainer.POSTGRES_JUNIT_PASSWORD
        )
    }

    companion object {
        private val CHANGELOG_PUBLIC_SCHEMA = "public"
        private val CHANGELOG_STANDARD_FILENAME = "changelog.xml"
        private val CHANGELOG_STANDARD_WITH_TEST_DATA_FILENAME = "changelog_migration_test.xml"
    }

    @Test
    fun `it should upgrade the standard database`() {
        // Exercise
        val SUT = PostgresLiquibaseUpgrade(db, UpgradeParams(
                CHANGELOG_STANDARD_FILENAME,
                CHANGELOG_PUBLIC_SCHEMA
        ))
        SUT.upgradeDatabase()

        // Verify
        open(db) {
            openStatement { statement ->
                statement.executeQuery("SELECT * FROM $CHANGELOG_PUBLIC_SCHEMA.documents").use { query ->
                    if (query.next()) {
                        assertFail("Did not expect any results")
                    }
                }
            }
        }
    }

    @Test
    fun `it should upgrade the standard database with initial test data`() {
        // Exercise
        val SUT = PostgresLiquibaseUpgrade(db, UpgradeParams(
                CHANGELOG_STANDARD_WITH_TEST_DATA_FILENAME,
                CHANGELOG_PUBLIC_SCHEMA
        ))
        SUT.upgradeDatabase()

        // Verify
        open(db) {
            openStatement { statement ->
                statement.executeQuery("SELECT * FROM $CHANGELOG_PUBLIC_SCHEMA.documents").use { query ->
                    if (query.next()) {
                        val timestampObj = query.getTimestamp("created_at")

                        val zdtAtZoneOfSample = timestampObj.toLocalDateTime().atZone(TestSample.zonedDateTime.zone)
                        assertThat(zdtAtZoneOfSample).isEqualTo(TestSample.zonedDateTime)

                    } else {
                        assertFail("Expected a result")
                    }

                    if (query.next()) {
                        assertFail("Too many results")
                    }
                }
            }
        }
    }
}