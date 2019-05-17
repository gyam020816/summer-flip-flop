package eu.ha3.x.sff.system.postgres

import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.SuspendingConnection
import com.github.jasync.sql.db.asSuspending
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
import eu.ha3.x.sff.json.KObjectMapper
import eu.ha3.x.sff.system.SDocPersistenceSystem
import org.postgresql.util.PGobject

/**
 * (Default template)
 * Created on 2019-01-23
 *
 * @author Ha3
 */
class JasyncPostgresDocPersistenceSystem(val db: DbConnectionParams) : SDocPersistenceSystem {
    init {
        Class.forName("org.postgresql.Driver")
    }

    private val connection: SuspendingConnection by lazy {
        PostgreSQLConnectionBuilder.createConnectionPool(
                "${db.jdbcUrl}?user=${db.user}&password=${db.pass}")
                .asSuspending
    }

    private val objectMapper = KObjectMapper.newInstance()

    override suspend fun listAll(): DocListResponse {
        val result: QueryResult = connection.sendPreparedStatement("SELECT * FROM public.documents ORDER BY created_at ASC")
        val documents = result.rows.map { query ->
            val data = query.getString("data")
            objectMapper.readValue(data, DocEntity::class.java).to()
        }

        return DocListResponse(documents)
    }

    override suspend fun appendToDocs(doc: Doc) {
        val documentSerialized = objectMapper.writeValueAsString(DocEntity.from(doc))

        connection.sendPreparedStatement("INSERT INTO public.documents (data, created_at) VALUES (?, ?)", listOf(
                PGobject().apply {
                    type = "jsonb"
                    value = documentSerialized
                },
                PGobject().apply {
                    type = "timestamp"
                    value = objectMapper.writeValueAsString(doc.createdAt)
                }))

        return NoMessage
    }
}