package com.coach.flame.api.client.request

import java.time.LocalDate

data class MeasureRequest(
    val value: Float?,
    val date: LocalDate?,
)
