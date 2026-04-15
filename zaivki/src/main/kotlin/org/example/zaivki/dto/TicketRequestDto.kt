package org.example.zaivki.dto

import org.example.zaivki.entity.Specialization

data class TicketRequestDto(
    val userId: Long,
    val description: String,
    val specialization: Specialization
)