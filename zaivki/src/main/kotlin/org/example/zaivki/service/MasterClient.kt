package org.example.zaivki.service

import org.example.zaivki.dto.MasterResponseDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class MasterClient(
    private val restTemplate: RestTemplate,
    private val retryTemplate: RetryTemplate,
    @Value("\${app.integration.master-service.url}")
    private val masterServiceUrl: String
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun getMasterById(masterId: Long): MasterResponseDto? {
        return retryTemplate.execute<MasterResponseDto?, Exception> {
            log.info("Попытка получить мастера ID $masterId...")
            restTemplate.getForObject("$masterServiceUrl/$masterId", MasterResponseDto::class.java)
        } ?: run {
            log.error("Не удалось получить мастера ID $masterId после всех попыток")
            null
        }
    }

    fun notifyInjury(lastName: String) {
        retryTemplate.execute<Unit, Exception> {
            val url = "$masterServiceUrl/injury?lastName=$lastName"
            restTemplate.patchForObject(url, null, Void::class.java)
            log.info("Уведомление о травме для $lastName отправлено")
        }
    }
}