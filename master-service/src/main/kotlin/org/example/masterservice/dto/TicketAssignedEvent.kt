package org.example.masterservice.dto

data class TicketAssignedEvent(
    val ticketId: Long,
    val masterId: Long
)