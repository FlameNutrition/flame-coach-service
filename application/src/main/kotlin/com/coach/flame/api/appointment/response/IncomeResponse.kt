package com.coach.flame.api.appointment.response

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class IncomeResponse(
    val incomes: Map<LocalDate, List<Float>>,
)
