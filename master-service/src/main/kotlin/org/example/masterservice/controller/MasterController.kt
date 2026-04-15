package org.example.masterservice.controller

import jakarta.validation.Valid
import org.example.masterservice.dto.MasterRegistrationDto
import org.example.masterservice.dto.TaskAssignmentDto
import org.example.masterservice.entity.Master
import org.example.masterservice.service.MasterService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/masters")
class MasterController(private val masterService: MasterService) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody dto: MasterRegistrationDto): ResponseEntity<String> {
        masterService.sendRegistration(dto)
        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .body("Запрос на регистрацию мастера ${dto.name} отправлен в Kafka!")
    }

    @PostMapping("/assign-task")
    fun assignTask(@Valid @RequestBody dto: TaskAssignmentDto): ResponseEntity<String> {
        masterService.assignTask(dto)
        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .body("Задача назначена!")
    }

    @PatchMapping("/injury")
    fun markAsInjured(@RequestParam lastName: String): ResponseEntity<Void> {
        masterService.reportMasterInjury(lastName)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<Master> {
        return ResponseEntity.ok(masterService.getMasterById(id))
    }
}