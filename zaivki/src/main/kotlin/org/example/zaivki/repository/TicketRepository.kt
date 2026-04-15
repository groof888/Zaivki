package org.example.zaivki.repository

import org.example.zaivki.entity.Ticket
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository : JpaRepository<Ticket, Long> {
    fun findAllByUserId(userId: Long): List<Ticket>
}