package org.example.masterservice.kafka

import org.example.masterservice.dto.MasterRegistrationDto
import org.example.masterservice.entity.*
import org.example.masterservice.repository.MasterRepository
import org.example.masterservice.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MasterListener(
    private val masterRepository: MasterRepository,
    private val userRepository: UserRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    @KafkaListener(topics = ["master-registration-topic"], groupId = "master-group")
    fun listenRegistration(dto: MasterRegistrationDto) {
        log.info("Получено сообщение на регистрацию мастера: $dto")

        val externalId = dto.userId ?: run {
            log.error("ОТКАЗ: Получено сообщение без userId")
            return
        }

        val existingUser = userRepository.findByExternalId(externalId)

        if (existingUser != null && masterRepository.findByUserName(existingUser.name ?: "") != null) {
            log.warn("ОТКАЗ: Мастер с externalId $externalId уже существует")
            return
        }

        val user = existingUser ?: userRepository.save(
            UserEntity(externalId = externalId, name = dto.name ?: "Anonymous")
        )

        val newMaster = Master(
            name = dto.name ?: "New Master",
            specialization = dto.specialization ?: "General",
            experienceYears = dto.experienceYears ?: 0,
            user = user,
            status = MasterStatus.AVAILABLE
        )

        masterRepository.save(newMaster)
        log.info("УСПЕХ: Мастер ${newMaster.name} зарегистрирован в базе.")
    }
}