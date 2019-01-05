package eu.ha3.x.sff.system

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * (Default template)
 * Created on 2019-01-05
 *
 * @author Ha3
 */
@Testcontainers
class PostgresDocSystemTest {
    class KPostgreSQLContainer(imageName: String) : PostgreSQLContainer<KPostgreSQLContainer>(imageName) {
        // See https://github.com/testcontainers/testcontainers-java/issues/318#issuecomment-290692749
    }

    private val POSTGRES_JUNIT_USERNAME = "test_junit_username"
    private val POSTGRES_JUNIT_PASSWORD = "test_junit_password"

    @Container
    private val pgContainer = KPostgreSQLContainer("postgres:9.6.2")
            .withUsername(POSTGRES_JUNIT_USERNAME)
            .withPassword(POSTGRES_JUNIT_PASSWORD)

    @Test
    fun `WIP it should init test container`() {
        assertThat(pgContainer.jdbcUrl).startsWith("jdbc:postgresql://")
    }
}