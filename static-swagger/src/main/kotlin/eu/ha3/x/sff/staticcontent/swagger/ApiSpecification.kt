package eu.ha3.x.sff.staticcontent.swagger
import io.swagger.v3.core.util.Yaml
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema

/**
 * (Default template)
 * Created on 2019-05-08
 *
 * @author Ha3
 */
object ApiSpecification {
    fun newInstance(): OpenAPI = has {
        val documentsTag = listOf("documents")
        val json = "application/json"
        info = has {
            title = "Summer Flip Flop Exploratory API"
            description = "This API contains sample routes for exploration purposes."
            version = "1.0.0"
        }
        addServersItem(has {
            url = "http://localhost:8080"
            description = "Local server"
        })
        paths = has {
            addPathItem("/docs", has {
                post = has {
                    description = "Create a doc"
                    tags = documentsTag
                    operationId = "addDoc"
                    requestBody = has {
                        required = true
                        content = has {
                            addMediaType(json, schemaReferenceTo("DocCreateRequest"))
                        }
                    }
                    responses = has {
                        addApiResponse("201", has {
                            content = has {
                                description = "Created doc"
                                addMediaType(json, schemaReferenceTo("Doc"))
                            }
                        })
                    }
                }
                get = has {
                    description = "List all docs"
                    tags = documentsTag
                    operationId = "listDocs"
                    responses = has {
                        addApiResponse("200", has {
                            content = has {
                                description = "List of all docs"
                                addMediaType(json, schemaReferenceTo("DocListResponse"))
                            }
                        })
                    }
                }
            })
        }
        schema("Doc", has {
            type = "object"
            addProperties("name", has {
                type = "string"
                description = "Name"
            })
            addProperties("createdAt", has {
                type = "string"
                format = "date-time"
                description = "Creation date-time, usually with 'Z' time-offset"
            })
        })
        schema("DocCreateRequest", has {
            type = "object"
            addProperties("name", has {
                type = "string"
                description = "Name"
            })
        })
        schema("DocListResponse", has<ArraySchema> {
            type = "array"
            items = referenceTo("Doc")
        })
    }
}

fun main() {
    val specification = ApiSpecification.newInstance()

    println(Yaml.mapper().writeValueAsString(specification))
}

private fun schemaReferenceTo(schemaName: String): MediaType {
    return has {
        schema = has {
            `$ref` = "#/components/schemas/$schemaName"
        }
    }
}

private fun referenceTo(schemaName: String): Schema<*> {
    return has {
        `$ref` = "#/components/schemas/$schemaName"
    }
}

private inline fun <reified T> has(block: T.() -> Unit): T =
        T::class.java.getDeclaredConstructor().newInstance().apply(block)
