package org.example.zaivki.client

import org.example.zaivki.dto.MasterResponseDto
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class MasterClient(private val restTemplate: RestTemplate) {

    private val masterServiceUrl = "http://localhost:8081/api/v1/masters"

    fun getMasterById(masterId: Long): MasterResponseDto? {
        return try {
            restTemplate.getForObject("$masterServiceUrl/$masterId", MasterResponseDto::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun notifyInjury(lastName: String) {
        try {
            val url = "$masterServiceUrl/injury?lastName=$lastName"
            restTemplate.patchForObject(url, null, Void::class.java)
        } catch (e: Exception) {
            println("Ошибка при уведомлении о травме: ${e.message}")
        }
    }
}