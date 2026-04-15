package org.example.masterservice.entity

import jakarta.persistence.*

enum class MasterStatus {
    AVAILABLE, BUSY, FIRED, INJURED
}

@Entity
@Table(name = "masters")
class Master(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val name: String,

    @Enumerated(EnumType.STRING)
    val specialization: Specialization,

    @Enumerated(EnumType.STRING)
    var status: MasterStatus = MasterStatus.AVAILABLE,

    val userId: Long
)