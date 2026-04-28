package org.example.zaivki

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.retry.annotation.EnableRetry

@SpringBootApplication
@EnableKafka
@EnableCaching
@EnableRetry
class ZaivkiApplication

fun main(args: Array<String>) {
    runApplication<ZaivkiApplication>(*args)
}
