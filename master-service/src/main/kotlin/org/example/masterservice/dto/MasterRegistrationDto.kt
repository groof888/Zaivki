// В файле MasterRegistrationDto.kt
package org.example.masterservice.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.example.masterservice.entity.Specialization

data class MasterRegistrationDto(
    @field:NotNull
    val userId: Long,
    @field:NotBlank
    val name: String,
    @field:NotNull
    val specialization: Specialization,
    val experienceYears: Int?
)