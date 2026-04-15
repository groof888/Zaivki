package org.example.masterservice.kafka

import org.example.masterservice.dto.TaskAssignmentDto
import org.example.masterservice.entity.EmergencyEvent
import org.example.masterservice.entity.MasterTask
import org.example.masterservice.repository.EmergencyEventRepository
import org.example.masterservice.repository.MasterTaskRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class InjuryListener(
    private val emergencyEventRepository: EmergencyEventRepository,
    private val taskRepository: MasterTaskRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["master-task-topic"], groupId = "master-service-group")
    fun handleTaskAssignment(dto: TaskAssignmentDto) {
        val newTask = MasterTask(
            masterId = dto.masterId,
            ticketId = dto.ticketId,
            description = dto.description,
            status = "ASSIGNED"
        )
        taskRepository.save(newTask)
        log.info("Задача для мастера ID ${dto.masterId} сохранена в таблицу master_tasks.")
    }

    @KafkaListener(topics = ["worker-injury-topic"], groupId = "master-service-group")
    fun handleInjury(event: EmergencyEvent) {
        emergencyEventRepository.save(event)
        log.error("БАЗА: ЧП зафиксировано для мастера с фамилией ${event.workerLastName}")
    }
}