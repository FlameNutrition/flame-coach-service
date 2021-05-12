package com.coach.flame.domain

import java.time.LocalDate
import java.util.Objects.hash

data class MeasureDto(
    val id: Long? = null,
    val date: LocalDate,
    val value: Float,
) {
    override fun toString(): String {
        return "MeasureDto(" +
                "date=$date, " +
                "value=$value" +
                ")"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MeasureDto

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return hash(id)
    }

}
