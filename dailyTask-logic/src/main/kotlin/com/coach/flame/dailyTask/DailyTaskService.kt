package com.coach.flame.dailyTask

import com.coach.flame.dailyTask.domain.DailyTaskDto
import org.springframework.stereotype.Service

//TODO: Write documentation

interface DailyTaskService {

    fun getDailyTaskById(taskId: Long): DailyTaskDto

    fun getDailyTasksByClient(clientId: Long): Set<DailyTaskDto>

    fun createDailyTask(dailyTask: DailyTaskDto): Pair<String, Boolean>

    fun deleteDailyTask(dailyTask: DailyTaskDto): Pair<String, Boolean>

}