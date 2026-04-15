package org.example.zaivki.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.example.zaivki.dto.*
import org.example.zaivki.entity.Specialization
import org.example.zaivki.service.TicketService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post

@WebMvcTest(TicketController::class)
class TicketControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var ticketService: TicketService

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `POST tickets should return 201 and created ticket`() {
        val request = TicketRequestDto(
            userId = 1L,
            description = "Не горит свет",
            specialization = Specialization.ELECTRICIAN
        )
        val response = TicketResponseDto(
            id = 100L,
            description = "Не горит свет",
            status = "NEW",
            userId = 1L
        )
        every { ticketService.createTicket(request) } returns response

        mockMvc.post("/api/v1/tickets") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id") { value(100) }
            jsonPath("$.status") { value("NEW") }
        }
    }

    @Test
    fun `GET all tickets should return list`() {
        val list = listOf(
            TicketResponseDto(1L, "desc1", "NEW", 1L),
            TicketResponseDto(2L, "desc2", "DONE", 2L)
        )
        every { ticketService.getAllForReport() } returns list

        mockMvc.get("/api/v1/tickets").andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(2) }
        }
    }

    @Test
    fun `GET ticket by id should return DTO`() {
        val response = TicketResponseDto(5L, "test", "IN_PROGRESS", 3L)
        every { ticketService.getTicketById(5L) } returns response

        mockMvc.get("/api/v1/tickets/5").andExpect {
            status { isOk() }
            jsonPath("$.id") { value(5) }
        }
    }

    @Test
    fun `PATCH completion should return 204`() {
        val completion = CompletionRequest(rating = 4, review = "Хорошо")
        every { ticketService.completeTicket(1L, 4, "Хорошо") } just runs

        mockMvc.patch("/api/v1/tickets/1/completion") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(completion)
        }.andExpect {
            status { isNoContent() }
        }
    }

    @Test
    fun `POST incidents should return 201`() {
        every { ticketService.handleInjury(3L, 999L) } just runs

        mockMvc.post("/api/v1/tickets/3/incidents") {
            param("masterId", "999")
        }.andExpect {
            status { isCreated() }
        }
    }
}