package org.example.masterservice.kafka

import org.example.masterservice.dto.MasterRegistrationDto
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class MasterProducerService(private val kafkaTemplate: KafkaTemplate<String, Any>) {

    fun sendRegistration(dto: MasterRegistrationDto) {
        kafkaTemplate.send("master-registration", dto)
    }
}