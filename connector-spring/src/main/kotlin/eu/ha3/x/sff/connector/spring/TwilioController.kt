package eu.ha3.x.sff.connector.spring

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * (Default template)
 * Created on 2018-07-22
 *
 * @author Ha3
 */
@RestController
class TwilioController {
    @GetMapping("/endpoint/text", produces = [MediaType.APPLICATION_XML_VALUE])
    fun pushText() = "<>"
}