package org.example.zaivki.dto

data class TicketRequestDto(
    val description: String,
    val employeeId: Long,
    val userId: Long // Передаем просто цифру ID
)