package com.coach.flame.api.dailyTask.request

data class DailyTaskFiltersRequest(
    val filters: Set<DailyTaskFilter> = setOf(),
)
