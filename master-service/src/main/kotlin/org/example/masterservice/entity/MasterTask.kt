package org.example.masterservice.entity

import jakarta.persistence.*

@Entity
@Table(name = "master_tasks")
class MasterTask(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val masterId: Long,
    val ticketId: Long,
    val description: String,
    var status: String = "IN_PROGRESS" // По умолчанию задача в работе
)