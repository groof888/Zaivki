package org.example.masterservice.repository

import org.example.masterservice.entity.MasterTask
import org.springframework.data.jpa.repository.JpaRepository

interface MasterTaskRepository : JpaRepository<MasterTask, Long>