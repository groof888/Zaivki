package org.example.masterservice.entity

import jakarta.persistence.*

enum class MasterStatus {
    AVAILABLE, BUSY, FIRED, INJURED
}

@Entity
@Table(name = "masters")
class Master(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val name: String,
    val specialization: String,
    val experienceYears: Int = 0,

    @Enumerated(EnumType.STRING)
    var status: MasterStatus = MasterStatus.AVAILABLE,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    val user: UserEntity
)