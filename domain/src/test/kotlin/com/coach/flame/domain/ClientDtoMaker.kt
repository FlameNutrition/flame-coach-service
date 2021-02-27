package com.coach.flame.domain

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.a
import com.natpryce.makeiteasy.MakeItEasy.make
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import com.natpryce.makeiteasy.PropertyLookup
import java.util.*

class ClientDtoMaker {

    companion object {

        private val fake = Faker()
        val identifier: Property<ClientDto, UUID> = newProperty()
        val firstName: Property<ClientDto, String> = newProperty()
        val lastName: Property<ClientDto, String> = newProperty()
        val birthday: Property<ClientDto, Date> = newProperty()
        val phoneCode: Property<ClientDto, String> = newProperty()
        val phoneNumber: Property<ClientDto, String> = newProperty()
        val country: Property<ClientDto, CountryDto> = newProperty()
        val gender: Property<ClientDto, GenderDto> = newProperty()
        val clientType: Property<ClientDto, ClientTypeDto> = newProperty()
        val loginInfo: Property<ClientDto, LoginInfoDto> = newProperty()

        val ClientDto: Instantiator<ClientDto> = Instantiator {
            ClientDto(
                identifier = it.valueOf(identifier, UUID.randomUUID()),
                firstName = it.valueOf(firstName, fake.name().firstName()),
                lastName = it.valueOf(lastName, fake.name().lastName()),
                birthday = it.valueOf(birthday, fake.date().birthday()),
                phoneCode = it.valueOf(phoneCode, fake.phoneNumber().extension()),
                phoneNumber = it.valueOf(phoneNumber, fake.phoneNumber().phoneNumber()),
                country = it.valueOf(country, make(a(CountryDtoMaker.CountryDto))),
                gender = it.valueOf(gender, make(a(GenderDtoMaker.GenderDto))),
                clientType = it.valueOf(clientType, ClientTypeDto.CLIENT),
                loginInfo = it.valueOf(loginInfo, make(a(LoginInfoDtoMaker.LoginInfoDto))),
            )
        }
    }

}