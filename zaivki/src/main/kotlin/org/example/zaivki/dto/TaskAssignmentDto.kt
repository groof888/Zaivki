package org.example.zaivki.dto

data class TaskAssignmentDto(
    val masterId: Long,        // ID мастера, которому даем задачу
    val ticketId: Long,        // ID самой заявки/тикета
    val description: String    // Краткое описание, что нужно сделать
)