package com.coach.flame.jpa.repository

import com.coach.flame.jpa.entity.DailyTask
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DailyTaskRepository : JpaRepository<DailyTask, Long>