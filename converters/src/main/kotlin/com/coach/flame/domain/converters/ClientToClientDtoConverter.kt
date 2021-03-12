package com.coach.flame.domain.converters

import com.coach.flame.domain.*
import com.coach.flame.jpa.entity.Client
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class ClientToClientDtoConverter(
    private val countryConfigToCountryDtoConverter: CountryConfigToCountryDtoConverter,
    private val genderConfigToGenderDtoConverter: GenderConfigToGenderDtoConverter
) : Converter<Client, ClientDto> {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ClientToClientDtoConverter::class.java)
    }

    override fun convert(client: Client): ClientDto {

        var countryDto: CountryDto? = null
        var genderDto: GenderDto? = null

        val clientTypeDto = try {
            CustomerTypeDto.valueOf(client.clientType.type.toUpperCase())
        } catch (ex: Exception) {
            LOGGER.warn("opr='convert', msg='Can not convert the client type', clientType={}", client.clientType.type)
            CustomerTypeDto.UNKNOWN
        }

        if (client.country !== null) {
            countryDto = countryConfigToCountryDtoConverter.convert(client.country!!)
        } else {
            LOGGER.warn("opr='convert', msg='Country is null', clientUUID={}", client.uuid)
        }

        if (client.gender !== null) {
            genderDto = genderConfigToGenderDtoConverter.convert(client.gender!!)
        } else {
            LOGGER.warn("opr='convert', msg='Gender is null', clientUUID={}", client.uuid)
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
            clientStatus = ClientStatusDto.valueOf(client.clientStatus.name)
        )
    }
}