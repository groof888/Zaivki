package org.example.masterservice.dto

data class TicketCreatedEvent(
    val ticketId: Long,
    val specialization: String
)