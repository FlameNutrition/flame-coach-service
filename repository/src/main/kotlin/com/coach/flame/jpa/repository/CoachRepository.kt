package com.coach.flame.jpa.repository

import com.coach.flame.jpa.entity.Coach
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CoachRepository : JpaRepository<Coach, Long> {

    fun findByUuid(uuid: UUID): Coach?

}