package org.example.zaivki.dto

data class TicketCreatedEvent(
    val ticketId: Long,
    val specialization: String
)