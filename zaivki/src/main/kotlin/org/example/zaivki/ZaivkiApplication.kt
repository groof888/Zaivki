package org.example.zaivki

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.kafka.annotation.EnableKafka

@SpringBootApplication
@EnableKafka
@EnableCaching
class ZaivkiApplication

fun main(args: Array<String>) {
    runApplication<ZaivkiApplication>(*args)
}
