package com.coach.flame.jpa.repository

import com.coach.flame.jpa.entity.DailyTask
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DailyTaskRepository : JpaRepository<DailyTask, Long> {

    @Query(
        "select * from DailyTask dt where dt.clientFk = :clientId",
        nativeQuery = true
    )
    fun findAllByClient(@Param("clientId") clientId: Long): Optional<Set<DailyTask>>

}