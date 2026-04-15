package org.example.masterservice.service

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.example.masterservice.dto.MasterInjuryEvent
import org.example.masterservice.dto.MasterRegistrationDto
import org.example.masterservice.entity.Master
import org.example.masterservice.entity.MasterStatus
import org.example.masterservice.entity.Specialization
import org.example.masterservice.kafka.MasterProducerService
import org.example.masterservice.repository.MasterRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.kafka.core.KafkaTemplate
import java.util.*

@ExtendWith(MockKExtension::class)
class MasterServiceTest {

    @MockK
    lateinit var masterRepository: MasterRepository

    @MockK
    lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @MockK
    lateinit var masterProducerService: MasterProducerService

    @InjectMockKs
    lateinit var masterService: MasterService

    @Test
    fun `getMasterById should return master if exists`() {
        val master = Master(id = 1L, name = "Ivan", specialization = Specialization.ELECTRICIAN, userId = 100L)
        every { masterRepository.findById(1L) } returns Optional.of(master)

        val result = masterService.getMasterById(1L)
        assertEquals(1L, result.id)
    }

    @Test
    fun `getMasterById should throw NoSuchElementException if not found`() {
        every { masterRepository.findById(99L) } returns Optional.empty()
        assertThrows(NoSuchElementException::class.java) {
            masterService.getMasterById(99L)
        }
    }

    @Test
    fun `sendRegistration should call producer service`() {
        val dto = MasterRegistrationDto(1L, "Petr", Specialization.PLUMBER, 5)
        every { masterProducerService.sendRegistration(dto) } just runs

        masterService.sendRegistration(dto)
        verify { masterProducerService.sendRegistration(dto) }
    }

    @Test
    fun `reportMasterInjury should set status and send Kafka event`() {
        val master = Master(id = 1L, name = "Ivan Ivanov", specialization = Specialization.ELECTRICIAN, userId = 100L)
        every { masterRepository.findAll() } returns listOf(master)
        every { masterRepository.save(any()) } returns master
        every { kafkaTemplate.send(any(), any()) } returns mockk()

        masterService.reportMasterInjury("Ivanov")

        assertEquals(MasterStatus.INJURED, master.status)
        verify { kafkaTemplate.send("master-injury-topic", MasterInjuryEvent(1L)) }
    }

    @Test
    fun `reportMasterInjury should throw NoSuchElementException if master not found`() {
        every { masterRepository.findAll() } returns emptyList()
        assertThrows(NoSuchElementException::class.java) {
            masterService.reportMasterInjury("Unknown")
        }
    }

    @Test
    fun `processInjury should update status when master exists`() {
        val master = Master(id = 1L, name = "Ivan", specialization = Specialization.ELECTRICIAN, userId = 100L)
        every { masterRepository.findById(1L) } returns Optional.of(master)
        every { masterRepository.save(any()) } returns master

        masterService.processInjury(MasterInjuryEvent(1L))

        assertEquals(MasterStatus.INJURED, master.status)
    }

    @Test
    fun `processInjury should do nothing if master not found`() {
        every { masterRepository.findById(99L) } returns Optional.empty()
        masterService.processInjury(MasterInjuryEvent(99L))
        verify(exactly = 0) { masterRepository.save(any()) }
    }
}