package org.example.masterservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.example.masterservice.dto.MasterRegistrationDto
import org.example.masterservice.dto.TaskAssignmentDto
import org.example.masterservice.entity.Master
import org.example.masterservice.entity.Specialization
import org.example.masterservice.service.MasterService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post

@WebMvcTest(MasterController::class)
class MasterControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var masterService: MasterService

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `POST register should return 202 Accepted`() {
        val dto = MasterRegistrationDto(1L, "Petr", Specialization.PLUMBER, 5)
        every { masterService.sendRegistration(dto) } just runs

        mockMvc.post("/api/v1/masters/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(dto)
        }.andExpect {
            status { isAccepted() }
            content { string("Запрос на регистрацию мастера Petr отправлен в Kafka!") }
        }
    }

    @Test
    fun `POST assign-task should return 202`() {
        val dto = TaskAssignmentDto(masterId = 1L, ticketId = 100L, description = "Fix leak")
        every { masterService.assignTask(dto) } just runs

        mockMvc.post("/api/v1/masters/assign-task") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(dto)
        }.andExpect {
            status { isAccepted() }
            content { string("Задача назначена!") }
        }
    }

    @Test
    fun `PATCH injury should return 200`() {
        every { masterService.reportMasterInjury("Ivanov") } just runs

        mockMvc.patch("/api/v1/masters/injury") {
            param("lastName", "Ivanov")
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `GET by id should return master`() {
        val master = Master(id = 1L, name = "Ivan", specialization = Specialization.ELECTRICIAN, userId = 100L)
        every { masterService.getMasterById(1L) } returns master

        mockMvc.get("/api/v1/masters/1").andExpect {
            status { isOk() }
            jsonPath("$.id") { value(1) }
            jsonPath("$.name") { value("Ivan") }
        }
    }
}