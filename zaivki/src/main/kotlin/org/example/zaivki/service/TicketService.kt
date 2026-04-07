package org.example.zaivki.service

import org.example.zaivki.dto.TicketRequestDto
import org.example.zaivki.dto.TicketResponseDto
import org.example.zaivki.entity.RequestStatus
import org.example.zaivki.entity.Ticket
import org.example.zaivki.entity.ServiceItem
import org.example.zaivki.repository.ServiceItemRepository
import org.example.zaivki.repository.TicketRepository
import org.example.zaivki.repository.UserRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class TicketService(
    private val ticketRepository: TicketRepository,
    private val userRepository: UserRepository,
    private val serviceItemRepository: ServiceItemRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    fun getAllForReport(): List<Ticket> = ticketRepository.findAll()

    @Transactional
    fun takeToWork(ticketId: Long, workerId: Long) {
        val ticket = ticketRepository.findById(ticketId)
            .orElseThrow { Exception("Заявка не найдена") }
        ticket.employeeId = workerId
        ticket.status = RequestStatus.IN_PROGRESS
        ticketRepository.save(ticket)
    }

    @Transactional
    fun completeTicket(ticketId: Long, rating: Int, review: String) {
        val ticket = ticketRepository.findById(ticketId)
            .orElseThrow { Exception("Заявка не найдена") }
        ticket.status = RequestStatus.DONE
        ticket.completedAt = LocalDateTime.now()
        ticket.rating = rating
        ticket.reviewText = review
        ticketRepository.save(ticket)
    }

    @Transactional
    fun createTicket(dto: TicketRequestDto): TicketResponseDto {
        val userEntity = userRepository.findById(dto.userId)
            .orElseThrow { Exception("Пользователь с ID ${dto.userId} не найден") }

        val ticket = Ticket(
            description = dto.description,
            employeeId = dto.employeeId,
            user = userEntity,
            status = RequestStatus.CREATED
        )

        val savedTicket = ticketRepository.save(ticket)

        return TicketResponseDto(
            id = savedTicket.id!!,
            description = savedTicket.description,
            status = savedTicket.status.name,
            userId = savedTicket.user.id!!
        )
    }

    @Cacheable(value = ["services"])
    fun getAvailableServices(): List<ServiceItem> {
        return serviceItemRepository.findAll()
    }

    @Transactional
    fun handleInjury(ticketId: Long, workerLastName: String) {
        val ticket = ticketRepository.findById(ticketId)
            .orElseThrow { Exception("Заявка не найдена") }

        ticket.status = RequestStatus.FAILED_INJURY
        ticketRepository.save(ticket)

        val message = """{"workerId": ${ticket.employeeId}, "lastName": "$workerLastName", "payout": 50000}"""
        kafkaTemplate.send("worker-injury-topic", message)
    }
}