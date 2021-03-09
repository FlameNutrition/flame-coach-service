package com.coach.flame.api.dailyTask.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DailyTaskResponse(
    val dailyTasks: Set<DailyTask> = setOf(),
)