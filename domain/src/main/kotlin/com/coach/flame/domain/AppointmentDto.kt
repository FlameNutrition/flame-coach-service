package com.coach.flame.domain

import java.time.ZonedDateTime
import java.util.*
import java.util.Objects.hash

class AppointmentDto(
    val id: Long? = null,
    val identifier: UUID,
    var dttmStarts: ZonedDateTime,
    var dttmEnds: ZonedDateTime,
    val delete: Boolean = false,
    var coach: CoachDto? = null,
    var client: ClientDto? = null,
    val currency: Currency = Currency.getInstance("GBP"),
    val income: IncomeDto,
    val notes: String?,
) {

    val safeCoach: CoachDto
        get() {
            checkNotNull(coach) { "coach can not be null" }
            return coach!!
        }

    val safeClient: ClientDto
        get() {
            checkNotNull(client) { "client can not be null" }
            return client!!
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AppointmentDto

        if (identifier != other.identifier) return false

        return true
    }

    override fun hashCode(): Int {
        return hash(identifier)
    }

    override fun toString(): String {
        return "AppointmentDto(" +
                "uuid=$identifier, " +
                "dttmStarts=$dttmStarts, " +
                "dttmEnds=$dttmEnds, " +
                "delete=$delete, " +
                "coach=$coach, " +
                "client=$client, " +
                "currency=$currency, " +
                "income=$income, " +
                "notes=$notes" +
                ")"
    }

}
