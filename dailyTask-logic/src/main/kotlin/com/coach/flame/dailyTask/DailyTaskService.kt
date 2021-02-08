package com.coach.flame.dailyTask

import com.coach.flame.dailyTask.domain.DailyTaskDto
import java.util.*

//TODO: Write documentation
interface DailyTaskService {

    fun getDailyTaskById(taskId: Long): DailyTaskDto

    fun getDailyTasksByClient(clientId: Long): Set<DailyTaskDto>

    fun createDailyTask(dailyTask: DailyTaskDto): UUID

    fun deleteDailyTask(uuid: UUID)

}