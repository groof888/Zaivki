package org.example.zaivki.service

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.example.zaivki.dto.*
import org.example.zaivki.entity.*
import org.example.zaivki.repository.TicketRepository
import org.example.zaivki.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.kafka.core.KafkaTemplate
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockKExtension::class)
class TicketServiceTest {

    @MockK
    lateinit var ticketRepository: TicketRepository

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @InjectMockKs
    lateinit var ticketService: TicketService

    @Test
    fun `createTicket should save ticket and send Kafka event`() {
        val dto = TicketRequestDto(
            userId = 1L,
            description = "Не работает розетка",
            specialization = Specialization.ELECTRICIAN
        )
        val user = User(id = 1L, firstName = "Иван", lastName = "Петров", address = "ул. Ленина, 1")

        val savedTicket = Ticket(
            id = 100L,
            description = dto.description,
            user = user,
            specialization = dto.specialization,
            status = RequestStatus.NEW,
            masterId = null,
            completedAt = null,
            rating = null,
            reviewText = null,
            createdAt = LocalDateTime.now()
        )

        every { userRepository.findById(1L) } returns Optional.of(user)
        every { ticketRepository.save(any()) } returns savedTicket
        every { kafkaTemplate.send(any(), any()) } returns mockk()

        val result = ticketService.createTicket(dto)

        assertEquals(100L, result.id)
        verify(exactly = 1) { ticketRepository.save(any()) }
        verify {
            kafkaTemplate.send(
                "ticket-created-topic",
                match { event: TicketCreatedEvent ->
                    event.ticketId == 100L && event.specialization == "ELECTRICIAN"
                }
            )
        }
    }

    @Test
    fun `createTicket should throw when user not found`() {
        val dto = TicketRequestDto(userId = 99L, description = "desc", specialization = Specialization.PLUMBER)
        every { userRepository.findById(99L) } returns Optional.empty()

        assertThrows(RuntimeException::class.java) {
            ticketService.createTicket(dto)
        }
        verify(exactly = 0) { ticketRepository.save(any()) }
        verify(exactly = 0) { kafkaTemplate.send(any(), any()) }
    }

    @Test
    fun `getAllForReport should return list of DTOs`() {
        val user = User(id = 1L, firstName = "A", lastName = "B", address = "C")
        val ticket1 = Ticket(
            id = 1L,
            description = "desc1",
            user = user,
            specialization = Specialization.ELECTRICIAN,
            status = RequestStatus.NEW
        )
        val ticket2 = Ticket(
            id = 2L,
            description = "desc2",
            user = user,
            specialization = Specialization.GAS_FITTER,
            status = RequestStatus.IN_PROGRESS
        )
        every { ticketRepository.findAll() } returns listOf(ticket1, ticket2)

        val result = ticketService.getAllForReport()

        assertEquals(2, result.size)
        assertEquals(1L, result[0].id)
        assertEquals("NEW", result[0].status)
    }

    @Test
    fun `getTicketById should return DTO when exists`() {
        val user = User(id = 1L, firstName = "A", lastName = "B", address = "C")
        val ticket = Ticket(id = 5L, description = "desc", user = user, specialization = Specialization.WELDER)
        every { ticketRepository.findById(5L) } returns Optional.of(ticket)

        val result = ticketService.getTicketById(5L)

        assertEquals(5L, result.id)
        assertEquals("desc", result.description)
    }

    @Test
    fun `getTicketById should throw when not found`() {
        every { ticketRepository.findById(99L) } returns Optional.empty()
        assertThrows(RuntimeException::class.java) {
            ticketService.getTicketById(99L)
        }
    }

    @Test
    fun `completeTicket should update status and rating`() {
        val ticket = Ticket(
            id = 1L,
            description = "desc",
            user = mockk(),
            specialization = Specialization.PLUMBER
        )
        every { ticketRepository.findById(1L) } returns Optional.of(ticket)
        every { ticketRepository.save(any()) } returns ticket

        ticketService.completeTicket(1L, 5, "Отлично!")

        assertEquals(RequestStatus.DONE, ticket.status)
        assertEquals(5, ticket.rating)
        assertEquals("Отлично!", ticket.reviewText)
        assertNotNull(ticket.completedAt)
        verify { ticketRepository.save(ticket) }
    }

    @Test
    fun `handleInjury should mark ticket as FAILED_INJURY and send Kafka event`() {
        val ticket = Ticket(
            id = 10L,
            description = "Сломалась труба",
            user = mockk(),
            specialization = Specialization.PLUMBER
        )
        every { ticketRepository.findById(10L) } returns Optional.of(ticket)
        every { ticketRepository.save(any()) } returns ticket
        every { kafkaTemplate.send(any(), any()) } returns mockk()

        ticketService.handleInjury(10L, 500L)

        assertEquals(RequestStatus.FAILED_INJURY, ticket.status)
        assertTrue(ticket.description.contains("ИНЦИДЕНТ"))
        verify {
            kafkaTemplate.send(
                "master-injury-topic",
                MasterInjuryEvent(500L)
            )
        }
    }

    @Test
    fun `handleAssignment should update masterId and status`() {
        val ticket = Ticket(
            id = 1L,
            description = "...",
            user = mockk(),
            specialization = Specialization.ELECTRICIAN
        )
        every { ticketRepository.findById(1L) } returns Optional.of(ticket)
        every { ticketRepository.save(any()) } returns ticket

        ticketService.handleAssignment(TicketAssignedEvent(1L, 777L))

        assertEquals(777L, ticket.masterId)
        assertEquals(RequestStatus.IN_PROGRESS, ticket.status)
        verify { ticketRepository.save(ticket) }
    }

    @Test
    fun `handleAssignment should ignore when ticket not found`() {
        every { ticketRepository.findById(any()) } returns Optional.empty()
        ticketService.handleAssignment(TicketAssignedEvent(999L, 123L))
        verify(exactly = 0) { ticketRepository.save(any()) }
    }
}