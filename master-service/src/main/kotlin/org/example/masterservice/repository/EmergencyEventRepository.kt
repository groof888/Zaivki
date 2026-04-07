package org.example.masterservice.repository

import org.example.masterservice.entity.EmergencyEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmergencyEventRepository : JpaRepository<EmergencyEvent, Long>