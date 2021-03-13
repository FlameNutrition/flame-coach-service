package com.coach.flame.jpa.repository

import com.coach.flame.jpa.entity.Client
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ClientRepository : JpaRepository<Client, Long> {

    fun findByUuid(uuid: UUID): Client?

    @Query("select c from Client c " +
            "where c.coach.uuid = :uuid and c.clientStatus in ('ACCEPTED', 'PENDING')")
    fun findClientsWithCoach(@Param("uuid") uuidCoach: UUID): List<Client>

    @Query("select c from Client c " +
            "where c.coach is null and c.clientStatus in ('AVAILABLE')")
    fun findClientsWithoutCoach(): List<Client>


}