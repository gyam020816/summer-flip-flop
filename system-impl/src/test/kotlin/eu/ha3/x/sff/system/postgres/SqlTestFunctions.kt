package eu.ha3.x.sff.system.postgres

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

/**
 * (Default template)
 * Created on 2019-01-12
 *
 * @author Ha3
 */
class DbConnection private constructor (val connection: Connection) {
    fun queryNow(query: String, processorFn: (ResultSet) -> Unit) {
        openStatement { statement ->
            statement.executeQuery(query).use(processorFn)
        }
    }

    fun execute(query: String) {
        openStatement { statement ->
            statement.executeUpdate(query)
        }
    }

    fun openStatement(block: (Statement) -> Unit) {
        connection.createStatement().use(block)
    }

    companion object {
        fun open(db: DbConnectionParams, processorFn: DbConnection.() -> Unit) {
            DriverManager.getConnection(db.jdbcUrl, db.user, db.pass).use { connection ->
                DbConnection(connection).processorFn()
            }
        }
    }
}