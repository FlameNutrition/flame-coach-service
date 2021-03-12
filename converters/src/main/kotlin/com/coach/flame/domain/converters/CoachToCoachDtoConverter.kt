package com.coach.flame.domain.converters

import com.coach.flame.domain.*
import com.coach.flame.jpa.entity.Coach
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class CoachToCoachDtoConverter(
    private val countryConfigToCountryDtoConverter: CountryConfigToCountryDtoConverter,
    private val genderConfigToGenderDtoConverter: GenderConfigToGenderDtoConverter,
    private val clientToClientDtoConverter: ClientToClientDtoConverter,
) : Converter<Coach, CoachDto> {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(CoachToCoachDtoConverter::class.java)
    }

    override fun convert(coach: Coach): CoachDto {

        var countryDto: CountryDto? = null
        var genderDto: GenderDto? = null

        val clientTypeDto = try {
            CustomerTypeDto.valueOf(coach.clientType.type.toUpperCase())
        } catch (ex: Exception) {
            LOGGER.warn("opr='convert', msg='Can not convert the client type', clientType={}", coach.clientType.type)
            CustomerTypeDto.UNKNOWN
        }

        if (coach.country !== null) {
            countryDto = countryConfigToCountryDtoConverter.convert(coach.country!!)
        } else {
            LOGGER.warn("opr='convert', msg='Country is null', clientUUID={}", coach.uuid)
        }

        if (coach.gender !== null) {
            genderDto = genderConfigToGenderDtoConverter.convert(coach.gender!!)
        } else {
            LOGGER.warn("opr='convert', msg='Gender is null', clientUUID={}", coach.uuid)
        }

        return CoachDto(
            identifier = coach.uuid,
            firstName = coach.firstName,
            lastName = coach.lastName,
            birthday = coach.birthday,
            phoneCode = coach.phoneCode,
            phoneNumber = coach.phoneNumber,
            country = countryDto,
            gender = genderDto,
            customerType = clientTypeDto,
            loginInfo = LoginInfoDto(
                username = coach.user.email,
                password = "******",
                expirationDate = coach.user.userSession.expirationDate,
                token = coach.user.userSession.token
            ),
            listOfClients = coach.clients
                .map { clientToClientDtoConverter.convert(it) }
                .toSet()
        )
    }
}