package com.coach.flame.api.dailyTask.request

data class DailyTaskRequest(
    val name: String?,
    val description: String?,
    val date: String?,
    val clientIdentifierTask: String?,
    val clientIdentifierCreator: String?
)