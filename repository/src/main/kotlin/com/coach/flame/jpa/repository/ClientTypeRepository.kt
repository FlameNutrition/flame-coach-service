package com.coach.flame.jpa.repository

import com.coach.flame.jpa.entity.ClientType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClientTypeRepository : JpaRepository<ClientType, Long> {

    fun getByType(type: String): ClientType?

}