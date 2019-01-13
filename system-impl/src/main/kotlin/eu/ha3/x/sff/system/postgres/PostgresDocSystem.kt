package eu.ha3.x.sff.system.postgres

import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
import eu.ha3.x.sff.core.SystemException
import eu.ha3.x.sff.system.IDocSystem
import eu.ha3.x.sff.system.postgres.PgUtil.Companion.open
import io.reactivex.Single
import org.postgresql.util.PGobject
import java.time.ZonedDateTime

/**
 * (Default template)
 * Created on 2019-01-05
 *
 * @author Ha3
 */
internal data class DocEntity(val name: String, val createdAt: ZonedDateTime) {
    companion object {
        fun from(doc: Doc)= DocEntity(doc.name, doc.createdAt)
    }
    fun to(): Doc = Doc(name, createdAt)
}

class PostgresDocSystem(val db: DbConnectionParams) : IDocSystem {
    init {
        Class.forName("org.postgresql.Driver")
    }

    private val objectMapper = KObjectMapper.newInstance()

    override fun listAll(): Single<DocListResponse> = Single.create { rx ->
        try {
            open(db) { connection ->
                connection.prepareCall("SELECT * FROM public.documents ORDER BY created_at ASC").use { statement ->
                    statement.executeQuery().use { query ->
                        val mutableDocuments = mutableListOf<Doc>()
                        while (query.next()) {
                            val data = query.getObject("data", PGobject::class.java)
                            val document = objectMapper.readValue(data.value, DocEntity::class.java).to()
                            mutableDocuments.add(document)
                        }

                        rx.onSuccess(DocListResponse(mutableDocuments.toList()))
                    }
                }
            }
        } catch (e: Exception) {
            rx.onError(SystemException(e))
        }
    }

    override fun appendToDocs(doc: Doc): Single<NoMessage> = Single.create { rx ->
        val documentSerialized = objectMapper.writeValueAsString(DocEntity.from(doc))

        try {
            open(db) { connection ->
                connection.autoCommit = false
                connection.prepareStatement("INSERT INTO public.documents (data, created_at) VALUES (?, ?)").use { statement ->
                    statement.setObject(1, PGobject().apply {
                        type = "jsonb"
                        value = documentSerialized
                    })
                    statement.setObject(2, PGobject().apply {
                        type = "timestamp"
                        value = objectMapper.writeValueAsString(doc.createdAt)
                    })

                    statement.executeUpdate()
                    connection.commit()

                    rx.onSuccess(NoMessage)
                }
            }
        } catch (e: Exception) {
            rx.onError(SystemException(e))
        }
    }
}