package eu.ha3.x.sff.system.postgres

import eu.ha3.x.sff.system.postgres.PgUtil.Companion.open
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.diff.output.DiffOutputControl
import liquibase.integration.commandline.CommandLineUtils
import liquibase.resource.ClassLoaderResourceAccessor
import liquibase.resource.CompositeResourceAccessor
import liquibase.resource.FileSystemResourceAccessor
import java.sql.Connection

/**
 * (Default template)
 * Created on 2019-01-09
 *
 * @author Ha3
 */
class PostgresLiquibaseUpgrade(private val db: DbConnectionParams, private val upgrade: UpgradeParams) {
    init {
        Class.forName("org.postgresql.Driver")
    }

    fun upgradeDatabase() {
        open(db) { connection ->
            val database = databaseFromConnection(connection)
            val liquibasex = Liquibase(upgrade.changelogFilename, accessor(), database)
            liquibasex.update(null as String?)
        }
    }

    fun generateChangelog() {
        open(db) { connection ->
            CommandLineUtils.doGenerateChangeLog(
                    upgrade.changelogFilename,
                    databaseFromConnection(connection),
                    null,
                    upgrade.schema,
                    null,
                    this.javaClass.simpleName,
                    null,
                    null,
                    DiffOutputControl()
            )
        }
    }

    private fun accessor() = CompositeResourceAccessor(
            ClassLoaderResourceAccessor(),
            FileSystemResourceAccessor(),
            ClassLoaderResourceAccessor(Thread.currentThread().contextClassLoader)
    )

    private fun databaseFromConnection(conn: Connection) =
            DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(conn))
}

data class UpgradeParams(val changelogFilename: String, val schema: String)