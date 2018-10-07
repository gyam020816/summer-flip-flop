package eu.ha3.x.sff.connector.spring

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync // FIXME: https://stackoverflow.com/questions/41985387/spring-async-not-allowing-use-of-autowired-beans
open class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}