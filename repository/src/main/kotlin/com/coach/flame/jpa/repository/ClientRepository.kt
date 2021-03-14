package com.coach.flame.jpa.repository

import com.coach.flame.jpa.entity.Client
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ClientRepository : JpaRepository<Client, Long> {

    companion object {
        private const val queryFindClientsWithoutCoach = "select * from Client c " +
                "where c.coachFk is null and c.clientStatus = 'AVAILABLE' " +
                "union " +
                "select c.* from Client c " +
                "join Coach coach on c.coachFk = coach.id " +
                "where coach.uuid = :uuid"

        private const val counterQueryFindClientsWithoutCoach = "select count(*) from (select * from Client c " +
                "where c.coachFk is null and c.clientStatus = 'AVAILABLE' " +
                "union " +
                "select c.* from Client c " +
                "join Coach coach on c.coachFk = coach.id " +
                "where coach.uuid = :uuid) as `availableClientsCounter`"
    }

    fun findByUuid(uuid: UUID): Client?

    @Query("select c from Client c " +
            "where c.coach.uuid = :uuid and c.clientStatus in ('ACCEPTED', 'PENDING')")
    fun findClientsWithCoach(@Param("uuid") uuidCoach: UUID): List<Client>

    @Query(value = queryFindClientsWithoutCoach, nativeQuery = true)
    fun findClientsForCoach(@Param("uuid") uuidCoach: String): List<Client>

}