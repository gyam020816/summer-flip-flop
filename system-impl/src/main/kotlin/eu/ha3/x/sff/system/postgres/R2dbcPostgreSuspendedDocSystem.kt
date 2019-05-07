package eu.ha3.x.sff.system.postgres

import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.json.KObjectMapper
import eu.ha3.x.sff.system.SDocSystem
import io.r2dbc.client.R2dbc
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * (Default template)
 * Created on 2019-05-05
 *
 * @author Ha3
 */
class R2dbcPostgreSuspendedDocSystem(val db: DbConnectionParams) : SDocSystem {
    private val configuration: PostgresqlConnectionConfiguration
    private val r2dbc: R2dbc

    init {
        val noProtocol = db.jdbcUrl.substring(db.jdbcUrl.indexOf("://") + 3)
        val colonIndex = noProtocol.indexOf(":")
        val slashIndex = noProtocol.indexOf("/")
        val host = noProtocol.substring(0, colonIndex)
        val port = noProtocol.substring(colonIndex + 1, slashIndex).toInt()
        val database = noProtocol.substring(slashIndex + 1)
        configuration = PostgresqlConnectionConfiguration.builder()
                .host(host)
                .port(port)
                .database(database)
                .username(db.user)
                .password(db.pass)
                .build()

        r2dbc = R2dbc(PostgresqlConnectionFactory(configuration))
    }

    private val objectMapper = KObjectMapper.newInstance()

    override suspend fun listAll(): DocListResponse = coroutineScope {
        val result = async {
            r2dbc.withHandle {
                it.select("SELECT * FROM public.documents ORDER BY created_at ASC")
                        .mapRow { row -> objectMapper.readValue(row.get("data") as String, DocEntity::class.java).to() }
            }
                    .collectList()
                    .block() as MutableList<Doc>
        }
        DocListResponse(result.await().toList())
    }

    override suspend fun appendToDocs(doc: Doc) {
        coroutineScope {
            val job = launch { insertDocument(doc) }
            job.join()
        }
    }

    private fun insertDocument(doc: Doc) {
        val documentSerialized = objectMapper.writeValueAsString(DocEntity.from(doc))

        r2dbc.useTransaction { handle ->
            handle.execute("INSERT INTO public.documents (data, created_at) VALUES ($1, $2)",
                    documentSerialized,
                    doc.createdAt.toInstant())
        }
                .block()
    }
}