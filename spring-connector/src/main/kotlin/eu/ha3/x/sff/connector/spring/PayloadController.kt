package eu.ha3.x.sff.connector.spring

import eu.ha3.x.sff.api.IPayloadStorage
import eu.ha3.x.sff.core.Greeting
import eu.ha3.x.sff.core.Payload
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
open class PayloadController {
    val counter = 0L

    @Autowired
    lateinit var payloadStorage: IPayloadStorage;

    @GetMapping("/greeting")
    fun greeting(@RequestParam(value = "name", defaultValue = "World") name: String) =
            Greeting(counter, "Hello, $name")

    @PostMapping("/payloads")
    fun uploadPayload(@RequestParam("file", required = false) file: MultipartFile?): ResponseEntity<Long> {
        return ResponseEntity.status(201).body(file?.size ?: -1)
    }

    @GetMapping("/payloads")
    fun getPayloads(): ResponseEntity<List<Payload>> {
        return ResponseEntity.status(200).body(payloadStorage.getAll())
    }
}