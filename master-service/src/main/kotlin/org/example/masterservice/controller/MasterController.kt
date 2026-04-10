package org.example.masterservice.controller

import org.example.masterservice.dto.MasterRegistrationDto
import org.example.masterservice.dto.TaskAssignmentDto
import org.example.masterservice.service.MasterService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/masters")
class MasterController(private val masterService: MasterService) {
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
    @PatchMapping("/injury")
    fun markAsInjured(@RequestParam lastName: String): ResponseEntity<Void> {
        masterService.reportMasterInjury(lastName)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<Any> {
        val master = masterService.getMasterById(id)
        return ResponseEntity.ok(master)
    }
}