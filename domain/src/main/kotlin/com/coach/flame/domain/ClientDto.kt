package com.coach.flame.domain

import java.time.LocalDate
import java.util.*
import java.util.Objects.hash

data class ClientDto(
    override val id: Long? = null,
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
    override val registrationDate: LocalDate,
    val weight: Float = 0.0f,
    val height: Float = 0.0f,
    val measureType: MeasureTypeDto = MeasureTypeDto.KG_CM,
    //FIXME: Can we remove the "?"...the client status in database is not nullable
    val clientStatus: ClientStatusDto?,
    val coach: CoachDto?,
    val weightMeasureTimeline : MutableList<MeasureWeightDto> = mutableListOf()
) : Customer {
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

    override fun toString(): String {
        return "ClientDto(" +
                "identifier=$identifier, " +
                "firstName='$firstName', " +
                "lastName='$lastName', " +
                "birthday=$birthday, " +
                "phoneCode=$phoneCode, " +
                "phoneNumber=$phoneNumber, " +
                "country=$country, " +
                "gender=$gender, " +
                "customerType=$customerType, " +
                "loginInfo=$loginInfo, " +
                "registrationDate=$registrationDate, " +
                "weight=$weight, " +
                "height=$height, " +
                "measureType=$measureType, " +
                "clientStatus=$clientStatus, " +
                "coach=$coach, " +
                "weightMeasureTimeline=$weightMeasureTimeline" +
                ")"
    }
}

