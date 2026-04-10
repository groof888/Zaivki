package org.example.masterservice.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "external_id", unique = true, nullable = false)
    val externalId: Long,

    var name: String? = null,
)