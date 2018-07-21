package restx

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

data class Greeting(val id: Long, val content: String)

@RestController
open class GreetingController {
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

interface IPayloadStorage {
    fun getAll(): List<Payload>
}

data class Payload(val id: String)

@SpringBootApplication
open class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}