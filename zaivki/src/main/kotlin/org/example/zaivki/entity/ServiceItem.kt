package org.example.zaivki.entity

import jakarta.persistence.*

@Entity
@Table(name = "services")
class ServiceItem(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val name: String,
    val price: Double
)