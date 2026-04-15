package org.example.zaivki.entity

import jakarta.persistence.*
import java.time.LocalDateTime

enum class RequestStatus {
    NEW, WAITING, IN_PROGRESS, PAUSED, DONE, FAILED_INJURY
}

@Entity
@Table(name = "tickets")
class Ticket(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var description: String,

    @Column(name = "employee_id")
    var masterId: Long? = null,

    @Enumerated(EnumType.STRING)
    var status: RequestStatus = RequestStatus.NEW,

    @Enumerated(EnumType.STRING)
    var specialization: Specialization,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User,

    var completedAt: LocalDateTime? = null,
    var rating: Int? = null,
    var reviewText: String? = null,

    val createdAt: LocalDateTime = LocalDateTime.now()
)