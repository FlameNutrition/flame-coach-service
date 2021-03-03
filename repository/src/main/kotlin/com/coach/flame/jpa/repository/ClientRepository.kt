package com.coach.flame.jpa.repository

import com.coach.flame.jpa.entity.Client
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ClientRepository : JpaRepository<Client, Long> {

    fun findByUuid(uuid: UUID): Client?

}