package eu.ha3.x.sff.system.postgres

import eu.ha3.x.sff.system.postgres.DbConnection.Companion.open
import eu.ha3.x.sff.test.assertFail
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.postgresql.util.PGobject
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

/**
 * (Default template)
 * Created on 2019-01-09
 *
 * @author Ha3
 */
@Testcontainers
class PostgresLiquibaseUpgradeTest {
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
        private val OUTPUT_FILENAME = "changelog_test_output.xml"
        private val OUTPUT_PATH = Paths.get(OUTPUT_FILENAME)
        private val INPUT_RESOURCE = "changelog_test_input.xml"
        private val SCHEMA = "my_schema"
    }

    @AfterEach
    internal fun tearDown() {
        Files.deleteIfExists(OUTPUT_PATH)
    }

    @Test
    fun `it should generate a changelog`() {
        // Setup
        open(db) {
            execute("CREATE SCHEMA $SCHEMA")
            execute("""CREATE TABLE $SCHEMA.my_table (
                id serial PRIMARY KEY,
                data JSONB NOT NULL
            )""")
        }

        // Exercise
        val SUT = PostgresLiquibaseUpgrade(db, UpgradeParams(
                OUTPUT_FILENAME,
                SCHEMA
        ))
        SUT.generateChangelog()

        // Verify
        val changelog = Files.readAllBytes(OUTPUT_PATH).toString(StandardCharsets.UTF_8)
        assertThat(changelog).containsSubsequence("""
        <createTable catalogName="test" schemaName="$SCHEMA" tableName="my_table">
            <column autoIncrement="true" name="id" type="SERIAL">
                <constraints primaryKey="true" primaryKeyName="my_table_pkey"/>
            </column>
            <column name="data" type="JSONB">
                <constraints nullable="false"/>
            </column>
        </createTable>
        """.trim().split("\n").toList().map(String::trim))
    }

    @Test
    fun `it should create the database`() {
        // Exercise
        val SUT = PostgresLiquibaseUpgrade(db, UpgradeParams(
                INPUT_RESOURCE,
                SCHEMA
        ))
        SUT.upgradeDatabase()

        // Verify
        open(db) {
            queryNow("SELECT * FROM $SCHEMA.my_table") { query ->
                while (query.next()) {
                    assertFail("Did not expect any results")
                }
            }
        }
    }

    @Test
    fun `it should create the database and allow writing to it`() {
        // Exercise
        val SUT = PostgresLiquibaseUpgrade(db, UpgradeParams(
                INPUT_RESOURCE,
                SCHEMA
        ))
        SUT.upgradeDatabase()
        open(db) {
            connection.autoCommit = false
            connection.prepareStatement("INSERT INTO $SCHEMA.my_table (data) VALUES (?)").use { statement ->
                statement.setObject(1, PGobject().apply {
                    type = "jsonb"
                    value = """{"hello": "world"}"""
                })
                statement.executeUpdate()
                connection.commit()
            }
        }

        // Verify
        open(db) {
            queryNow("SELECT * FROM $SCHEMA.my_table") { query ->
                if (query.next()) {
                    val obj: PGobject = query.getObject("data", PGobject::class.java)
                    assertThat(obj.value).isEqualTo("""{"hello": "world"}""")

                } else {
                    assertFail("Expected a result but got none")
                }

                if (query.next()) {
                    assertFail("Too many results")
                }
            }
        }
    }
}