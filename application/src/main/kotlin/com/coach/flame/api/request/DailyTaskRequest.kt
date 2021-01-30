package com.coach.flame.api.request

data class DailyTaskRequest(
    var name: String,
    var description: String,
    var status: String
)
