package com.coach.flame.domain

import java.time.LocalDate
import java.util.*
import java.util.Objects.hash

data class CoachDto(
    override val identifier: UUID,
    override val firstName: String,
    override val lastName: String,
    override val birthday: LocalDate? = null,
    override val phoneCode: String? = null,
    override val phoneNumber: String? = null,
    override val country: CountryDto? = null,
    override val gender: GenderDto? = null,
    override val customerType: CustomerTypeDto,
    override val loginInfo: LoginInfoDto?,
    val listOfClients: Set<ClientDto> = mutableSetOf(),
) : Customer {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CoachDto

        if (identifier != other.identifier) return false

        return true
    }

    override fun hashCode(): Int {
        return hash(identifier)
    }
}

