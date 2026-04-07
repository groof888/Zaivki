package org.example.masterservice.repository

import org.example.masterservice.entity.Master
import org.example.masterservice.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MasterRepository : JpaRepository<Master, Long> {

    fun findByName(name: String): Master?

    fun findByUser(user: UserEntity): Master?
}