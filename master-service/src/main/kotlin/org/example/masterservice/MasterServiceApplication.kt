package org.example.masterservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MasterServiceApplication

fun main(args: Array<String>) {
    runApplication<MasterServiceApplication>(*args)
}
