package org.example.masterservice.kafka

import org.example.masterservice.dto.TicketAssignedEvent
import org.example.masterservice.dto.TicketCreatedEvent
import org.example.masterservice.entity.MasterStatus
import org.example.masterservice.entity.Specialization
import org.example.masterservice.repository.MasterRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class TicketListener(
    private val masterRepository: MasterRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["ticket-created-topic"], groupId = "ticket-group-v100")
    @Transactional
    fun listenTicketCreated(event: TicketCreatedEvent) {
        log.info("Новая заявка: ${event.ticketId}, спец: ${event.specialization}")

        val spec = try {
            Specialization.valueOf(event.specialization.uppercase())
        } catch (e: Exception) {
            log.error("Неверная специализация: ${event.specialization}")
            return
        }

        val master = masterRepository.findFirstBySpecializationAndStatus(spec, MasterStatus.AVAILABLE)

        if (master != null) {
            master.status = MasterStatus.BUSY
            masterRepository.save(master)

            kafkaTemplate.send("ticket-assigned-topic", TicketAssignedEvent(event.ticketId, master.userId))
            log.info("Мастер ${master.name} (ID: ${master.userId}) назначен на заявку ${event.ticketId}")
        } else {
            log.warn("Нет свободных мастеров для $spec")
        }
    }
}