package com.coach.flame.domain

import java.time.LocalDate
import java.util.*
import java.util.Objects.hash

data class DailyTaskDto(
    val identifier: UUID,
    val name: String,
    val description: String,
    val date: LocalDate,
    val ticked: Boolean,
    val coachIdentifier: UUID?,
    val clientIdentifier: UUID?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DailyTaskDto

        if (identifier != other.identifier) return false

        return true
    }

    override fun hashCode(): Int {
        return hash(identifier)
    }
}
