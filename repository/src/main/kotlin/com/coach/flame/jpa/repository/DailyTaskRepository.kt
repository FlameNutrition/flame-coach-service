package com.coach.flame.jpa.repository

import com.coach.flame.jpa.entity.DailyTask
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DailyTaskRepository : JpaRepository<DailyTask, Long>, JpaSpecificationExecutor<DailyTask> {

    @Query("select dt from DailyTask dt, Client c " +
            "where dt.client = c and c.uuid = :uuid")
    fun findAllByClient(@Param("uuid") uuid: UUID): Optional<Set<DailyTask>>

    fun findByUuid(uuid: UUID): DailyTask?

    fun deleteByUuid(uuid: UUID): Int

}