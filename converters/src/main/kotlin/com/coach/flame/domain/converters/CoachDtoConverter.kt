package com.coach.flame.domain.converters

import com.coach.flame.domain.*
import com.coach.flame.jpa.entity.Coach
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class CoachDtoConverter(
    private val countryDtoConverter: CountryDtoConverter,
    private val genderDtoConverter: GenderDtoConverter,
) : Converter<Coach, CoachDto> {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(CoachDtoConverter::class.java)
    }

    override fun convert(coach: Coach): CoachDto {

        var countryDto: CountryDto? = null
        var genderDto: GenderDto? = null

        val clientTypeDto = try {
            CustomerTypeDto.valueOf(coach.clientType.type.toUpperCase())
        } catch (ex: Exception) {
            LOGGER.warn("opr='convert', msg='Can not convert the customer type', customerType={}", coach.clientType.type)
            CustomerTypeDto.UNKNOWN
        }

        if (coach.country !== null) {
            countryDto = countryDtoConverter.convert(coach.country!!)
        } else {
            LOGGER.debug("opr='convert', msg='Country is null', clientUUID={}", coach.uuid)
        }

        if (coach.gender !== null) {
            genderDto = genderDtoConverter.convert(coach.gender!!)
        } else {
            LOGGER.debug("opr='convert', msg='Gender is null', clientUUID={}", coach.uuid)
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
                .map {
                    ClientDto(
                        identifier = it.uuid,
                        firstName = it.firstName,
                        lastName = it.lastName,
                        birthday = it.birthday,
                        phoneCode = it.phoneCode,
                        phoneNumber = it.phoneNumber,
                        country = it.country?.let { country -> countryDtoConverter.convert(country) },
                        gender = it.gender?.let { gender -> genderDtoConverter.convert(gender) },
                        customerType = CustomerTypeDto.CLIENT,
                        loginInfo = LoginInfoDto(
                            username = it.user.email,
                            password = "******"
                        ),
                        clientStatus = ClientStatusDto.valueOf(it.clientStatus.name),
                        registrationDate = it.registrationDate,
                        coach = null)
                }
                .toSet(),
            registrationDate = coach.registrationDate
        )
    }
}