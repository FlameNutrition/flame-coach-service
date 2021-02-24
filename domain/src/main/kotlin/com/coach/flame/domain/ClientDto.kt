package com.coach.flame.domain

import java.util.*
import java.util.Objects.hash

data class ClientDto(
    val identifier: UUID,
    val firstName: String? = null,
    val lastName: String? = null,
    val birthday: Date? = null,
    val phoneCode: String? = null,
    val phoneNumber: Int? = null,
    val country: CountryDto? = null,
    val gender: GenderDto? = null,
    val clientType: ClientTypeDto? = null,
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

