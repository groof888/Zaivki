package org.example.masterservice.repository

import org.example.masterservice.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByExternalId(externalId: Long): UserEntity?
}