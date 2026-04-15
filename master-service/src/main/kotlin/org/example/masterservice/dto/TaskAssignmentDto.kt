package org.example.masterservice.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class TaskAssignmentDto(
    @field:NotNull
    val masterId: Long,
    @field:NotNull
    val ticketId: Long,
    @field:NotBlank
    val description: String
)