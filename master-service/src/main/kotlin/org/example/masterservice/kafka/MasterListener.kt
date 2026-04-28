package org.example.masterservice.kafka

import org.example.masterservice.dto.MasterRegistrationDto
import org.example.masterservice.entity.*
import org.example.masterservice.repository.MasterRepository
import org.example.masterservice.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MasterListener(
    private val masterRepository: MasterRepository,
    private val userRepository: UserRepository,
    private val retryTemplate: RetryTemplate
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    @KafkaListener(topics = ["master-registration-topic"], groupId = "master-group-v100")
    fun listenRegistration(dto: MasterRegistrationDto) {
        retryTemplate.execute<Unit, Exception> {
            log.info("Регистрация мастера: $dto")
            val externalId = dto.userId ?: return@execute

            val user = userRepository.findByExternalId(externalId) ?: userRepository.save(
                UserEntity(externalId = externalId, name = dto.name ?: "Anonymous")
            )

            if (masterRepository.findByUserId(externalId) == null) {
                masterRepository.save(Master(
                    name = dto.name ?: "New Master",
                    specialization = dto.specialization ?: Specialization.ELECTRICIAN,
                    userId = externalId,
                    status = MasterStatus.AVAILABLE
                ))
                log.info("Мастер ${dto.name} зарегистрирован.")
            }
        }
    }
}