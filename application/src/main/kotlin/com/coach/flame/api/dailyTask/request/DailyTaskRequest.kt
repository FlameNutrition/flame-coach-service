package com.coach.flame.api.dailyTask.request

data class DailyTaskRequest(
    val taskName: String?,
    val taskDescription: String?,
    val ticked: Boolean?,
    val date: String?,
    val toDate: String?
)
