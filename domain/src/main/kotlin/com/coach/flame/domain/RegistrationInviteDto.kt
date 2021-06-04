package com.coach.flame.domain

import java.time.LocalDateTime
import java.util.Objects.hash

data class RegistrationInviteDto(
    val id: Long? = null,
    val sender: CoachDto,
    val sendTo: String,
    val registrationLink: String,
    val registrationKey: String,
    val sendDttm: LocalDateTime,
    val acceptedDttm: LocalDateTime?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RegistrationInviteDto

        if (registrationKey != other.registrationKey) return false

        return true
    }

    override fun hashCode(): Int {
        return hash(registrationKey)
    }

    override fun toString(): String {
        return "RegistrationInviteDto(" +
                "sender=$sender, " +
                "sendTo='$sendTo', " +
                "sendDttm=$sendDttm, " +
                "acceptedDttm=$acceptedDttm, " +
                ")"
    }
}

