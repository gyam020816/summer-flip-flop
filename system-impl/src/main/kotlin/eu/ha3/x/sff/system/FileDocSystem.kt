package eu.ha3.x.sff.system

import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
import io.reactivex.Single
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors

/**
 * (Default template)
 * Created on 2018-12-09
 *
 * @author Ha3
 */
class FileDocSystem(private val folder: Path) : IDocSystem {
    init {
        if (!Files.isDirectory(folder)) {
            throw IllegalArgumentException("$folder is not a directory")
        }
    }

    override fun listAll() = Single.create<DocListResponse> { source ->
        val result = Files.walk(folder, 2)
                .filter { !Files.isDirectory(it) }
                .map {
                    val content = String(Files.readAllBytes(it), StandardCharsets.UTF_8)
                    val firstSpace = content.indexOf(' ')
                    Doc(
                            createdAt = ZonedDateTime.parse(content.substring(0, firstSpace)),
                            name = content.substring(firstSpace + 1)
                    )
                }
                .sorted(Comparator.comparing(Doc::createdAt))
                .collect(Collectors.toList())
                .toList()

        source.onSuccess(DocListResponse(result))
    }

    override fun appendToDocs(doc: Doc) = Single.create<NoMessage> { source ->
        val content = """${DateTimeFormatter.ISO_ZONED_DATE_TIME.format(doc.createdAt)} ${doc.name}"""

        val file = folder.resolve(UUID.randomUUID().toString())
        content.byteInputStream(StandardCharsets.UTF_8).use { input ->
            Files.newOutputStream(file, StandardOpenOption.CREATE_NEW).use { output ->
                input.copyTo(output)
            }
        }

        source.onSuccess(NoMessage())
    }
}
