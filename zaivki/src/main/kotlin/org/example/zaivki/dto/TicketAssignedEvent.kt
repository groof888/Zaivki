package org.example.zaivki.dto

data class TicketAssignedEvent(
    val ticketId: Long,
    val masterId: Long
)