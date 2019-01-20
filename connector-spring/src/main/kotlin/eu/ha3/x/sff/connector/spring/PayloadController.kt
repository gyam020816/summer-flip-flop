package eu.ha3.x.sff.connector.spring

import eu.ha3.x.sff.api.RxDocStorage
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocCreateRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture

@RestController
open class PayloadController {
    @Autowired
    private lateinit var docStorage: RxDocStorage

    @PostMapping(value = ["/docs"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun uploadPayload(@RequestBody docCreateRequest: DocCreateRequest): ResponseEntity<Doc> {
        val futureResponse = CompletableFuture<ResponseEntity<Doc>>()

        docStorage.appendToDocs(docCreateRequest).subscribe { success ->
            futureResponse.complete(ResponseEntity.status(201).body(success))
        }

        return futureResponse.join()
    }

    @GetMapping("/docs")
    //@Async // FIXME: https://stackoverflow.com/questions/41985387/spring-async-not-allowing-use-of-autowired-beans
    fun getDocs(): ResponseEntity<List<Doc>> {
        val futureResponse = CompletableFuture<ResponseEntity<List<Doc>>>()

        docStorage.listAll().subscribe({ success ->
            futureResponse.complete(ResponseEntity.status(200).body(success.data))

        }, { error ->
            futureResponse.complete(ResponseEntity.status(500).build())
        })

        return futureResponse.join()
    }
}