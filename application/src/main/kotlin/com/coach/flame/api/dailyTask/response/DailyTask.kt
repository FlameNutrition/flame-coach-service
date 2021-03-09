package com.coach.flame.api.dailyTask.response

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.Objects.hash

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DailyTask(
    val identifier: String? = null,
    val taskName: String? = null,
    val taskDescription: String? = null,
    val date: String? = null,
    val ticked: Boolean? = null,
)