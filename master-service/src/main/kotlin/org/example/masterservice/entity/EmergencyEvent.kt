package org.example.masterservice.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "emergency_events")
class EmergencyEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "worker_id", nullable = false)
    val workerId: Long,

    @Column(name = "worker_last_name", nullable = false)
    val workerLastName: String,

    @Column(name = "payout_amount", nullable = false)
    val payoutAmount: BigDecimal = BigDecimal("50000.00"),

    @Column(name = "event_date", nullable = false)
    val eventDate: LocalDateTime = LocalDateTime.now()
)