package com.coach.flame.api.dailyTask.response

import com.coach.flame.failure.domain.ErrorDetail
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DailyTaskResponse(
    val error: ErrorDetail? = null,
    val dailyTasks: Set<DailyTask>? = null
)