package com.coach.flame.api

import com.coach.flame.api.request.DailyTaskRequest
import com.coach.flame.api.response.DailyTaskResponse
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import org.springframework.web.bind.annotation.*

//TODO: Write documentation
interface DailyTaskAPI {

    fun createDailyTasks(dailyTasks: List<DailyTaskRequest>): DailyTaskResponse

    fun createDailyTask(dailyTask: DailyTaskRequest): DailyTaskResponse

    fun getDailyTasksByClient(clientId: Long): DailyTaskResponse

    fun getDailyTaskById(taskId: Long): DailyTaskResponse

    fun deleteDailyTaskById(taskUuid: String): DailyTaskResponse

}