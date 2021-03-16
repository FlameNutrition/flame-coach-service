package com.coach.flame.domain.converters

import com.coach.flame.domain.*
import com.coach.flame.jpa.entity.Client
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class ClientDtoConverter(
    private val countryDtoConverter: CountryDtoConverter,
    private val genderDtoConverter: GenderDtoConverter,
) : Converter<Client, ClientDto> {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ClientDtoConverter::class.java)
    }

    override fun convert(client: Client): ClientDto {

        var countryDto: CountryDto? = null
        var genderDto: GenderDto? = null

        val clientTypeDto = try {
            CustomerTypeDto.valueOf(client.clientType.type.toUpperCase())
        } catch (ex: Exception) {
            LOGGER.warn("opr='convert', msg='Can not convert the customer type', customerType={}", client.clientType.type)
            CustomerTypeDto.UNKNOWN
        }

        if (client.country !== null) {
            countryDto = countryDtoConverter.convert(client.country!!)
        } else {
            LOGGER.debug("opr='convert', msg='Country is null', clientUUID={}", client.uuid)
        }

        if (client.gender !== null) {
            genderDto = genderDtoConverter.convert(client.gender!!)
        } else {
            LOGGER.debug("opr='convert', msg='Gender is null', clientUUID={}", client.uuid)
        }

        return ClientDto(
            identifier = client.uuid,
            firstName = client.firstName,
            lastName = client.lastName,
            birthday = client.birthday,
            phoneCode = client.phoneCode,
            phoneNumber = client.phoneNumber,
            country = countryDto,
            gender = genderDto,
            customerType = clientTypeDto,
            loginInfo = LoginInfoDto(
                username = client.user.email,
                password = "******",
                expirationDate = client.user.userSession.expirationDate,
                token = client.user.userSession.token
            ),
            clientStatus = ClientStatusDto.valueOf(client.clientStatus.name),
            registrationDate = client.registrationDate,
            coach = client.coach?.let {
                CoachDto(
                    identifier = client.coach!!.uuid,
                    firstName = client.coach!!.firstName,
                    lastName = client.coach!!.lastName,
                    birthday = client.coach!!.birthday,
                    phoneCode = client.coach!!.phoneCode,
                    phoneNumber = client.coach!!.phoneNumber,
                    country = client.coach!!.country?.let { countryDtoConverter.convert(it) },
                    gender = client.coach!!.gender?.let { genderDtoConverter.convert(it) },
                    customerType = CustomerTypeDto.COACH,
                    loginInfo = null,
                    listOfClients = setOf(),
                    registrationDate = client.coach!!.registrationDate
                )
            }
        )
    }
}