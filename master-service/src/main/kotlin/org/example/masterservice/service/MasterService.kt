package org.example.masterservice.service

import org.example.masterservice.dto.*
import org.example.masterservice.entity.*
import org.example.masterservice.kafka.MasterProducerService
import org.example.masterservice.repository.MasterRepository
import org.example.masterservice.repository.MasterTaskRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MasterService(
    private val masterRepository: MasterRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val masterProducerService: MasterProducerService
) {
    private val log = LoggerFactory.getLogger(javaClass)


    fun getMasterById(id: Long): Master {
        return masterRepository.findById(id)
            .orElseThrow { NoSuchElementException("Мастер с ID $id не найден") }    }

    fun sendRegistration(dto: MasterRegistrationDto) {
        masterProducerService.sendRegistration(dto)
        log.info("Запрос на регистрацию мастера {} отправлен в Kafka", dto.name)
    }

    fun assignTask(dto: TaskAssignmentDto) {
        kafkaTemplate.send("master-task-topic", dto)
        log.info("Ручное назначение задачи для мастера ID {} отправлено в Kafka", dto.masterId)
    }

    @Transactional
    fun reportMasterInjury(lastName: String) {
        val master = masterRepository.findAll().find { it.name.contains(lastName, ignoreCase = true) }
            ?: throw NoSuchElementException("Мастер с фамилией $lastName не найден")
        master.status = MasterStatus.INJURED
        masterRepository.save(master)
        kafkaTemplate.send("master-injury-topic", MasterInjuryEvent(master.id!!))
        log.error("Мастер {} (ID: {}) помечен как травмированный", master.name, master.id)
    }

    @KafkaListener(topics = ["master-injury-topic"], groupId = "master-group")
    @Transactional
    fun processInjury(event: MasterInjuryEvent) {
        val master = masterRepository.findById(event.masterId).orElse(null) ?: return

        master.status = MasterStatus.INJURED
        masterRepository.save(master)

        saveInjuryPayout(master)
        log.error("Мастер ID {} травмирован (событие из Kafka). Выплата оформлена.", event.masterId)
    }

    private fun saveInjuryPayout(master: Master) {
        log.info("Данные о выплате мастеру {} отправлены в финансовый модуль", master.name)
    }
}