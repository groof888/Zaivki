package org.example.zaivki.repository

import org.example.zaivki.entity.ServiceItem
import org.springframework.data.jpa.repository.JpaRepository

interface ServiceItemRepository : JpaRepository<ServiceItem, Long>