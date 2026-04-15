package org.example.zaivki.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class CompletionRequest(
    @field:Min(1) @field:Max(5)
    val rating: Int,
    @field:NotBlank
    val review: String
)