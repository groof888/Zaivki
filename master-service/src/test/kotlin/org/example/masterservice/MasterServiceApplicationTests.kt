package org.example.masterservice.service

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.example.masterservice.entity.Master
import org.example.masterservice.entity.MasterStatus
import org.example.masterservice.entity.UserEntity
import org.example.masterservice.repository.MasterRepository
import org.example.masterservice.kafka.MasterProducerService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class MasterServiceTest {

    @MockK
    lateinit var masterRepository: MasterRepository

    @MockK
    lateinit var masterProducerService: MasterProducerService

    @InjectMockKs
    lateinit var masterService: MasterService

    @Test
    fun `reportMasterInjury should set status to INJURED when master exists`() {
        val lastName = "Иванов"

        val mockUser = UserEntity(id = 1L, externalId = 123L, name = "Иван")

        val mockMaster = Master(
            id = 1L,
            name = "Иван",
            specialization = "Слесарь",
            experienceYears = 5,
            status = MasterStatus.AVAILABLE,
            user = mockUser
        )

        every { masterRepository.findByUserName(lastName) } returns mockMaster
        every { masterRepository.save(any()) } returns mockMaster

        masterService.reportMasterInjury(lastName)

        assertEquals(MasterStatus.INJURED, mockMaster.status)
        verify(exactly = 1) { masterRepository.save(any()) }
    }

    @Test
    fun `reportMasterInjury should throw exception when master not found`() {
        val lastName = "Неизвестный"
        every { masterRepository.findByUserName(lastName) } returns null

        val exception = assertThrows(RuntimeException::class.java) {
            masterService.reportMasterInjury(lastName)
        }

        assertTrue(exception.message!!.contains("не найден"))
        verify(exactly = 0) { masterRepository.save(any()) }
    }
}