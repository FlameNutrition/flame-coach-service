package com.coach.flame.domain

import java.time.LocalDate
import java.util.*
import java.util.Objects.hash

data class ClientDto(
    val identifier: UUID,
    val firstName: String,
    val lastName: String,
    val birthday: LocalDate? = null,
    val phoneCode: String? = null,
    val phoneNumber: String? = null,
    val country: CountryDto? = null,
    val gender: GenderDto? = null,
    val clientType: ClientTypeDto,
    val loginInfo: LoginInfoDto?,
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

