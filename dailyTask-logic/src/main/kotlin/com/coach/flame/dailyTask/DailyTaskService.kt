package com.coach.flame.dailyTask

import com.coach.flame.dailyTask.filter.Filter
import com.coach.flame.domain.DailyTaskDto
import java.time.LocalDate
import java.util.*

//TODO: Write documentation
interface DailyTaskService {

    fun getDailyTaskById(taskId: Long): DailyTaskDto

    fun getDailyTasksUsingFilters(filters: Set<Filter>): Set<DailyTaskDto>

    fun getDailyTasksByClient(uuid: UUID): Set<DailyTaskDto>

    fun createDailyTask(dailyTask: DailyTaskDto, toDttm: LocalDate): Set<DailyTaskDto>

    fun createDailyTask(dailyTask: DailyTaskDto): DailyTaskDto

    fun updateDailyTask(dailyTask: DailyTaskDto): DailyTaskDto

    fun deleteDailyTask(uuid: UUID)

}
