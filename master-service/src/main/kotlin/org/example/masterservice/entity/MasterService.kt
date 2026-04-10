package org.example.masterservice.service

import org.example.masterservice.dto.MasterRegistrationDto
import org.example.masterservice.dto.TaskAssignmentDto
import org.example.masterservice.entity.Master
import org.example.masterservice.entity.MasterStatus
import org.example.masterservice.repository.MasterRepository
import org.springframework.stereotype.Service
import org.example.masterservice.kafka.MasterProducerService

@Service
class MasterService(
    private val masterRepository: MasterRepository,
    private val masterProducerService: MasterProducerService
) {

    fun getMasterById(id: Long): Master {
        return masterRepository.findById(id)
            .orElseThrow { RuntimeException("Мастер с ID $id не найден") }
    }

    fun reportMasterInjury(lastName: String) {
        val master = masterRepository.findByUserName(lastName)
            ?: throw RuntimeException("Мастер с фамилией $lastName не найден")

        master.status = MasterStatus.INJURED
        masterRepository.save(master)
    }

    fun assignTask(dto: TaskAssignmentDto) {
        val master = masterRepository.findById(dto.masterId)
            .orElseThrow { RuntimeException("Мастер с ID ${dto.masterId} не найден") }

        master.status = MasterStatus.BUSY
        masterRepository.save(master)
    }

    fun sendRegistration(dto: MasterRegistrationDto) {
        masterProducerService.sendRegistration(dto)
        println("Регистрация мастера ${dto.name} через Kafka (пока заглушка)")
    }
}