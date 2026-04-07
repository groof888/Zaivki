package org.example.zaivki.service

import MasterRegistrationDto
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.kafka.core.KafkaTemplate

@ExtendWith(MockitoExtension::class)
class MasterProducerServiceTest {

    // 1. Мокаем (создаем фальшивый) KafkaTemplate, чтобы не отправлять реальные данные в сеть
    @Mock
    lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    // 2. Внедряем наш фальшивый KafkaTemplate в реальный сервис
    @InjectMocks
    lateinit var masterProducerService: MasterProducerService

    @Test
    fun `should send master registration message to kafka`() {
        // Arrange (Подготовка данных)
        val dto = MasterRegistrationDto(
            name = "Иван",
            specialization = "Электрик",
            experienceYears = 5
        )

        // Act (Вызов реального метода, который мы тестируем)
        masterProducerService.sendRegistration(dto) // Если метод называется иначе, поменяй здесь

        // Assert (Проверка результата)
        // Проверяем, что мок KafkaTemplate действительно был вызван с нужным топиком и нашим DTO
        verify(kafkaTemplate).send("master-registration-topic", dto)
    }
}