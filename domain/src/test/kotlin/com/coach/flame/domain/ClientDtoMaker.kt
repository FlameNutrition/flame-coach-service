package com.coach.flame.domain

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDate
import java.util.*

class ClientDtoMaker {

    companion object {

        private val fake = Faker()
        val identifier: Property<ClientDto, UUID> = newProperty()
        val firstName: Property<ClientDto, String> = newProperty()
        val lastName: Property<ClientDto, String> = newProperty()
        val birthday: Property<ClientDto, LocalDate?> = newProperty()
        val phoneCode: Property<ClientDto, String?> = newProperty()
        val phoneNumber: Property<ClientDto, String?> = newProperty()
        val country: Property<ClientDto, CountryDto?> = newProperty()
        val gender: Property<ClientDto, GenderDto?> = newProperty()
        val customerType: Property<ClientDto, CustomerTypeDto> = newProperty()
        val loginInfo: Property<ClientDto, LoginInfoDto?> = newProperty()
        val clientStatus: Property<ClientDto, ClientStatusDto> = newProperty()

        val ClientDto: Instantiator<ClientDto> = Instantiator {
            ClientDto(
                identifier = it.valueOf(identifier, UUID.randomUUID()),
                firstName = it.valueOf(firstName, fake.name().firstName()),
                lastName = it.valueOf(lastName, fake.name().lastName()),
                birthday = it.valueOf(birthday, null as LocalDate?),
                phoneCode = it.valueOf(phoneCode, null as String?),
                country = it.valueOf(country, null as CountryDto?),
                gender = it.valueOf(gender, null as GenderDto?),
                customerType = it.valueOf(customerType, CustomerTypeDto.CLIENT),
                loginInfo = it.valueOf(loginInfo, null as LoginInfoDto?),
                clientStatus = it.valueOf(clientStatus, ClientStatusDto.PENDING)
            )
        }
    }

}