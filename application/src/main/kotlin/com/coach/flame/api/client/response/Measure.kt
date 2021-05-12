package com.coach.flame.api.client.response

import java.time.LocalDate

data class Measure(
    val identifier: Long,
    val date: LocalDate,
    val value: Float,
)
