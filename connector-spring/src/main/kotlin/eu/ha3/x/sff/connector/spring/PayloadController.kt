package eu.ha3.x.sff.connector.spring

import eu.ha3.x.sff.api.IDocStorage
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.Greeting
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.concurrent.CompletableFuture

@RestController
open class PayloadController {
    @Autowired
    private lateinit var docStorage: IDocStorage;

    @GetMapping("/greeting")
    fun greeting(@RequestParam(value = "name", defaultValue = "World") name: String) =
            Greeting(0L, "Hello, $name")

    @PostMapping("/docs")
    fun uploadPayload(@RequestParam("file", required = false) file: MultipartFile?): ResponseEntity<Long> {
        return ResponseEntity.status(201).body(file?.size ?: -1)
    }

    @GetMapping("/docs")
    //@Async // FIXME: https://stackoverflow.com/questions/41985387/spring-async-not-allowing-use-of-autowired-beans
    fun getDocs(): ResponseEntity<List<Doc>> {
        val futureResponse = CompletableFuture<ResponseEntity<List<Doc>>>()

        docStorage.listAll().subscribe({ success ->
            futureResponse.complete(ResponseEntity.status(200).body(success))

        }, { error ->
            futureResponse.complete(ResponseEntity.status(500).build())
        })

        return futureResponse.join()
    }
}