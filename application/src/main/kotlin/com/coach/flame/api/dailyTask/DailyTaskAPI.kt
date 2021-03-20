package com.coach.flame.api.dailyTask

import com.coach.flame.api.dailyTask.request.DailyTaskFiltersRequest
import com.coach.flame.api.dailyTask.request.DailyTaskRequest
import com.coach.flame.api.dailyTask.response.DailyTaskResponse
import java.util.*

//TODO: Write documentation
interface DailyTaskAPI {

    fun createDailyTasks(dailyTasks: List<DailyTaskRequest>): DailyTaskResponse

    fun createDailyTask(clientIdentifier: UUID, coachIdentifier: UUID, dailyTask: DailyTaskRequest): DailyTaskResponse

    fun getDailyTasksByClient(clientIdentifier: UUID): DailyTaskResponse

    fun getDailyTasksUsingFilters(request: DailyTaskFiltersRequest): DailyTaskResponse

    fun updateDailyTask(taskUUID: UUID, request: DailyTaskRequest): DailyTaskResponse

    fun deleteDailyTask(taskUUID: String): DailyTaskResponse

}