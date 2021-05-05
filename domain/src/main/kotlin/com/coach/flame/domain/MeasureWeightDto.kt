package com.coach.flame.domain

import java.time.LocalDate

class MeasureWeightDto(
    val id: Long? = null,
    val date: LocalDate,
    val value: Float,
) {
    override fun toString(): String {
        return "MeasureWeightDto(" +
                "date=$date, " +
                "value=$value" +
                ")"
    }
}
