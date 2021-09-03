package com.coach.flame.domain

import java.time.LocalDate
import java.util.*

class DateIntervalDto(val from: LocalDate, val to: LocalDate) {

    init {
        check(!from.isAfter(to)) { "from: $from can not be after to: $to" }
        check(!from.isEqual(to)) { "from: $from can not be equal to: $to" }
    }

    fun isBetweenInterval(date: LocalDate): Boolean {
        return !date.isBefore(from) && !date.isAfter(to)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DateIntervalDto

        if (from != other.from) return false
        if (to != other.to) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(from, to)
    }

    override fun toString(): String {
        return "DateIntervalDto(" +
                "from=$from, " +
                "to=$to)"
    }

}
