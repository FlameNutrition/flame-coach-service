package com.coach.flame.domain.converters

import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.ClientTypeDto
import com.coach.flame.domain.CountryDto
import com.coach.flame.domain.GenderDto
import com.coach.flame.jpa.entity.Client
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class ClientDtoConverter(
    @Autowired private val countryDtoConverter: CountryDtoConverter,
    @Autowired private val genderDtoConverter: GenderDtoConverter
) : Converter<Client, ClientDto> {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ClientDtoConverter::class.java)
    }

    override fun convert(client: Client): ClientDto {

        var countryDto: CountryDto? = null
        var genderDto: GenderDto? = null

        val clientTypeDto = try {
            ClientTypeDto.valueOf(client.clientType.type.toUpperCase())
        } catch (ex: Exception) {
            LOGGER.warn("opr='convert', msg='Can not convert the client type', clientType={}", client.clientType.type)
            ClientTypeDto.UNKNOWN
        }

        if (client.country !== null) {
            countryDto = countryDtoConverter.convert(client.country!!)
        } else {
            LOGGER.warn("opr='convert', msg='Country is null', clientUUID={}", client.uuid)
        }

        if (client.gender !== null) {
            genderDto = genderDtoConverter.convert(client.gender!!)
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
            clientType = clientTypeDto
        )
    }
}