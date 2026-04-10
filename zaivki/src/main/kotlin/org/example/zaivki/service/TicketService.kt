package org.example.zaivki.service

import org.example.zaivki.client.MasterClient
import org.example.zaivki.dto.TicketRequestDto
import org.example.zaivki.dto.TicketResponseDto
import org.example.zaivki.entity.RequestStatus
import org.example.zaivki.entity.Ticket
import org.example.zaivki.repository.TicketRepository
import org.example.zaivki.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class TicketService(
    private val ticketRepository: TicketRepository,
    private val userRepository: UserRepository,
    private val masterClient: MasterClient
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun createTicket(dto: TicketRequestDto): TicketResponseDto {
        val userEntity = userRepository.findById(dto.userId)
            .orElseThrow { RuntimeException("Пользователь не найден") }

        val ticket = Ticket(
            description = dto.description,
            user = userEntity,
            status = RequestStatus.CREATED
        )
        return mapToResponseDto(ticketRepository.save(ticket))
    }

    fun getAllForReport(): List<TicketResponseDto> =
        ticketRepository.findAll().map { mapToResponseDto(it) }

    fun getTicketById(id: Long): TicketResponseDto {
        val entity = ticketRepository.findById(id)
            .orElseThrow { RuntimeException("Заявка с ID $id не найдена") }
        return mapToResponseDto(entity)
    }

    @Transactional
    fun takeToWork(ticketId: Long, workerId: Long) {
        val master = masterClient.getMasterById(workerId)
            ?: throw RuntimeException("Мастер с ID $workerId не найден в системе мастеров")

        val ticket = ticketRepository.findById(ticketId)
            .orElseThrow { RuntimeException("Заявка с ID $ticketId не найдена") }

        ticket.status = RequestStatus.IN_PROGRESS
        ticket.employeeId = master.id

        // 4. Логирование
        log.info("Заявка #{} принята в работу мастером: {} (ID: {})", ticketId, master.name, master.id)

        ticketRepository.save(ticket)
    }

    @Transactional
    fun completeTicket(id: Long, rating: Int, review: String) {
        val ticket = ticketRepository.findById(id)
            .orElseThrow { RuntimeException("Заявка не найдена") }

        ticket.status = RequestStatus.DONE
        ticket.rating = rating
        ticket.reviewText = review
        ticket.completedAt = LocalDateTime.now()

        log.info("Заявка #{} успешно завершена с рейтингом {}", id, rating)
        ticketRepository.save(ticket)
    }

    @Transactional
    fun handleInjury(id: Long, workerLastName: String) {
        masterClient.notifyInjury(workerLastName)

        val ticket = ticketRepository.findById(id)
            .orElseThrow { RuntimeException("Заявка не найдена") }

        ticket.status = RequestStatus.FAILED_INJURY
        ticket.description = "${ticket.description} [ИНЦИДЕНТ: Травма сотрудника $workerLastName]"

        log.warn("ВНИМАНИЕ: Травма мастера {} на заявке #{}", workerLastName, id)
        ticketRepository.save(ticket)
    }

    private fun mapToResponseDto(entity: Ticket): TicketResponseDto {
        return TicketResponseDto(
            id = entity.id ?: 0,
            description = entity.description,
            status = entity.status.name,
            userId = entity.user.id ?: 0
        )
    }
}