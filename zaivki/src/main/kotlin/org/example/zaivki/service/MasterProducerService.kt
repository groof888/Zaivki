package org.example.zaivki.service

import MasterRegistrationDto
import org.example.zaivki.dto.TaskAssignmentDto
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class MasterProducerService(private val kafkaTemplate: KafkaTemplate<String, Any>) {
    fun sendRegistration(dto: MasterRegistrationDto) {
        // Отправляем объект в топик
        kafkaTemplate.send("master-registration-topic", dto)
    }
    fun assignTask(dto: TaskAssignmentDto) {
        kafkaTemplate.send("master-task-topic", dto)
        println("Задача ${dto.ticketId} отправлена мастеру ${dto.masterId}")
    }
}