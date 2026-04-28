package org.example.masterservice.kafka

import org.example.masterservice.dto.MasterRegistrationDto
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Service

@Service
class MasterProducerService(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val retryTemplate: RetryTemplate
) {
    fun sendRegistration(dto: MasterRegistrationDto) {
        retryTemplate.execute<Unit, Exception> {
            kafkaTemplate.send("master-registration", dto)
        }
    }
}