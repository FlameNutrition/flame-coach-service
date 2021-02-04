package com.coach.flame.dailyTask.domain

import java.util.*
import java.util.Objects.hash

data class ClientDto(
    val identifier: UUID
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClientDto

        if (identifier != other.identifier) return false

        return true
    }

    override fun hashCode(): Int {
        return hash(identifier)
    }
}

