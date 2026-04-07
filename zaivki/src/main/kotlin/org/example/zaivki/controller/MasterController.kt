package org.example.zaivki.controller

import MasterRegistrationDto
import org.example.zaivki.dto.TaskAssignmentDto
import org.example.zaivki.service.MasterProducerService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/masters")
class MasterController(private val masterService: MasterProducerService) {

    @PostMapping("/register")
    fun register(@RequestBody dto: MasterRegistrationDto): String {
        masterService.sendRegistration(dto)
        return "Запрос на регистрацию мастера ${dto.name} отправлен в Kafka!"
    }
    @PostMapping("/assign-task")
    fun assignTask(@RequestBody dto: TaskAssignmentDto): String {
        masterService.assignTask(dto)
        return "Задача назначена!"
    }
}