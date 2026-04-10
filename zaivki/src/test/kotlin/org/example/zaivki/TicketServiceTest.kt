package org.example.zaivki.service

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.example.zaivki.client.MasterClient
import org.example.zaivki.dto.MasterResponseDto
import org.example.zaivki.entity.RequestStatus
import org.example.zaivki.entity.Ticket
import org.example.zaivki.entity.User
import org.example.zaivki.repository.TicketRepository
import org.example.zaivki.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class TicketServiceTest {

    @MockK
    lateinit var ticketRepository: TicketRepository

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var masterClient: MasterClient

    @InjectMockKs
    lateinit var ticketService: TicketService

    @Test
    fun `takeToWork should assign ticket when master exists`() {
        // 1. Arrange
        val ticketId = 100L
        val workerId = 2L

        val mockUser = User(
            id = 1L,
            firstName = "Иван",
            lastName = "Иванов",
            address = "ул. Пушкина, 10"
        )

        val mockTicket = Ticket(
            id = ticketId,
            description = "Сломался кран",
            user = mockUser,
            status = RequestStatus.CREATED
        )

        val mockMasterDto = MasterResponseDto(
            id = workerId,
            name = "Диман",
            specialization = "слесарь",
            contactInfo = "89991234567"
        )

        every { masterClient.getMasterById(workerId) } returns mockMasterDto
        every { ticketRepository.findById(ticketId) } returns Optional.of(mockTicket)
        every { ticketRepository.save(any()) } returns mockTicket

        ticketService.takeToWork(ticketId, workerId)

        assertEquals(RequestStatus.IN_PROGRESS, mockTicket.status)
        assertEquals(workerId, mockTicket.employeeId)
        verify(exactly = 1) { ticketRepository.save(any()) }
    }
}