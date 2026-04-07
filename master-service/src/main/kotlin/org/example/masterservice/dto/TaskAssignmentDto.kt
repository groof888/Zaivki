package org.example.masterservice.dto

data class TaskAssignmentDto(
    val masterId: Long,
    val ticketId: Long,
    val description: String
)