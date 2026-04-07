package org.example.zaivki.dto

data class TicketResponseDto(
    val id: Long,
    val description: String,
    val status: String,
    val userId: Long
)