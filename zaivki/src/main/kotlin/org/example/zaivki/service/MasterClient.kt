package org.example.zaivki.service

import org.example.zaivki.dto.MasterResponseDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Component
class MasterClient(
    private val restTemplate: RestTemplate,
    @Value("\${app.integration.master-service.url}")
    private val masterServiceUrl: String
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun getMasterById(masterId: Long): MasterResponseDto? {
        return try {
            restTemplate.getForObject("$masterServiceUrl/$masterId", MasterResponseDto::class.java)
        } catch (e: Exception) {
            log.error("Ошибка при получении мастера ID $masterId: ${e.message}", e)
            null
        }
    }

    fun notifyInjury(lastName: String) {
        try {
            val url = "$masterServiceUrl/injury?lastName=$lastName"
            restTemplate.patchForObject(url, null, Void::class.java)
        } catch (e: HttpClientErrorException) {
            log.error("Ошибка уведомления о травме: ${e.statusCode} - ${e.responseBodyAsString}")
        } catch (e: Exception) {
            log.error("Неизвестная ошибка при уведомлении о травме: ${e.message}", e)
        }
    }
}