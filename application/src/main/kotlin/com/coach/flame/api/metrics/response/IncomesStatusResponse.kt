package com.coach.flame.api.metrics.response

data class IncomesStatusResponse(
    val accepted: Int = 0,
    val pending: Int = 0,
    val rejected: Int = 0,
)
