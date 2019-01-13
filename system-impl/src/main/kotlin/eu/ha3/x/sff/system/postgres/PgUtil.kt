package eu.ha3.x.sff.system.postgres

import java.sql.Connection
import java.sql.DriverManager

/**
 * (Default template)
 * Created on 2019-01-12
 *
 * @author Ha3
 */
internal fun open(db: DbConnectionParams, connectionFn: (connection: Connection) -> Unit) {
    DriverManager.getConnection(db.jdbcUrl, db.user, db.pass).use(connectionFn)
}
