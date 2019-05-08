package eu.ha3.x.sff.connector.kgraphql

import com.github.pgutkowski.kgraphql.KGraphQL
import eu.ha3.x.sff.api.SDocStorage
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocCreateRequest
import kotlinx.coroutines.runBlocking
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * (Default template)
 * Created on 2019-05-08
 *
 * @author Ha3
 */
class KGraphqlSchema(private val storage: SDocStorage) {
    private val schema = KGraphQL.schema {
        configure {
            useDefaultPrettyPrinter = true
        }

        query("docs") {
            resolver { ->
                runBlocking {
                    storage.listAll().data
                }
            }
        }

        mutation("createDoc") {
            resolver { name: String ->
                runBlocking {
                    storage.appendToDocs(DocCreateRequest(name))
                }
            }
        }

        type<Doc>()
        stringScalar<ZonedDateTime> {
            serialize = DateTimeFormatter.ISO_ZONED_DATE_TIME::format
            deserialize = ZonedDateTime::parse
        }
    }

    fun schema() = schema
}