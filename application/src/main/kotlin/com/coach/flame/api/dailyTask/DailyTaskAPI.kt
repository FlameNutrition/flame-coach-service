package com.coach.flame.api.dailyTask

import com.coach.flame.api.dailyTask.request.DailyTaskRequest
import com.coach.flame.api.dailyTask.response.DailyTaskResponse

//TODO: Write documentation
interface DailyTaskAPI {

    fun createDailyTasks(dailyTasks: List<DailyTaskRequest>): DailyTaskResponse

    fun createDailyTask(dailyTask: DailyTaskRequest): DailyTaskResponse

    fun getDailyTasksByClient(clientId: Long): DailyTaskResponse

    fun getDailyTaskById(taskId: Long): DailyTaskResponse

    fun deleteDailyTaskById(taskUuid: String): DailyTaskResponse

}