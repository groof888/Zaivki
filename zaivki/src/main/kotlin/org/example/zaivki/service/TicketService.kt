package org.example.zaivki.service

import org.example.zaivki.dto.*
import org.example.zaivki.entity.RequestStatus
import org.example.zaivki.entity.Ticket
import org.example.zaivki.entity.User
import org.example.zaivki.repository.TicketRepository
import org.example.zaivki.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


@Service
class TicketService(
    private val ticketRepository: TicketRepository,
    private val userRepository: UserRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val retryTemplate: RetryTemplate
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun createTicket(dto: TicketRequestDto): TicketResponseDto {
        return retryTemplate.execute<TicketResponseDto, Exception> {
            val user = userRepository.findById(dto.userId)
                .orElseThrow { NoSuchElementException("Пользователь не найден") }

            val ticket = Ticket(
                description = dto.description,
                user = user,
                specialization = dto.specialization,
                status = RequestStatus.NEW
            )
            val saved = ticketRepository.save(ticket)

            kafkaTemplate.send("ticket-created-topic", TicketCreatedEvent(
                saved.id!!,
                saved.specialization.name
            ))

            log.info("Создана заявка #{}", saved.id)
            mapToResponseDto(saved)
        }
    }

    @Transactional
    fun completeTicket(id: Long, rating: Int, review: String) {
        val ticket = ticketRepository.findById(id)
            .orElseThrow { RuntimeException("Заявка не найдена") }

        ticket.status = RequestStatus.DONE
        ticket.rating = rating
        ticket.reviewText = review
        ticket.completedAt = LocalDateTime.now()
        ticketRepository.save(ticket)
        log.info("Заявка #{} завершена", id)
    }

    @Transactional
    fun handleInjury(ticketId: Long, masterId: Long) {
        val ticket = ticketRepository.findById(ticketId)
            .orElseThrow { RuntimeException("Заявка не найдена") }

        ticket.status = RequestStatus.FAILED_INJURY
        ticket.description += " [ИНЦИДЕНТ: Травма мастера ID $masterId]"
        ticketRepository.save(ticket)

        kafkaTemplate.send("master-injury-topic", MasterInjuryEvent(masterId))
        log.warn("Травма мастера ID {} на заявке #{}", masterId, ticketId)
    }

    @KafkaListener(topics = ["ticket-assigned-topic"], groupId = "zaivki-group")
    @Transactional
    fun handleAssignment(event: TicketAssignedEvent) {
        retryTemplate.execute<Unit, Exception> {
            val ticket = ticketRepository.findById(event.ticketId).orElse(null) ?: return@execute
            ticket.masterId = event.masterId
            ticket.status = RequestStatus.IN_PROGRESS
            ticketRepository.save(ticket)
        }
    }
    fun getAllTickets(): List<Ticket> = ticketRepository.findAll()
    fun getTicketById(id: Long): Ticket? = ticketRepository.findById(id).orElse(null)

    private fun mapToResponseDto(entity: Ticket): TicketResponseDto = TicketResponseDto(
        id = entity.id ?: 0,
        description = entity.description,
        status = entity.status.name,
        userId = entity.user.id ?: 0
    )
}