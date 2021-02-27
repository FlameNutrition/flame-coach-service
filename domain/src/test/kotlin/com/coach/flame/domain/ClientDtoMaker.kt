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

        val nullableFields: Property<ClientDto, List<String>> = newProperty()

        private fun checkNullableField(fieldName: String, it: PropertyLookup<ClientDto>): Any? {

            if (it.valueOf(nullableFields, listOf()).contains(fieldName)) {
                return null
            }

            when (fieldName) {
                "birthday" -> return it.valueOf(birthday, fake.date().birthday())
                "phoneCode" -> return it.valueOf(phoneCode, fake.phoneNumber().extension())
                "phoneNumber" -> return it.valueOf(phoneNumber, fake.phoneNumber().phoneNumber())
                "country" -> return it.valueOf(country, make(a(CountryDtoMaker.CountryDto)))
                "gender" -> return it.valueOf(gender, make(a(GenderDtoMaker.GenderDto)))
                "loginInfo" -> return it.valueOf(loginInfo, make(a(LoginInfoDtoMaker.LoginInfoDto)))
            }

            return null
        }

        val ClientDto: Instantiator<ClientDto> = Instantiator {
            ClientDto(
                identifier = it.valueOf(identifier, UUID.randomUUID()),
                firstName = it.valueOf(firstName, fake.name().firstName()),
                lastName = it.valueOf(lastName, fake.name().lastName()),
                birthday = checkNullableField("birthday", it) as Date?,
                phoneCode = checkNullableField("phoneCode", it) as String?,
                phoneNumber = checkNullableField("phoneNumber", it) as String?,
                country = checkNullableField("country", it) as CountryDto?,
                gender = checkNullableField("gender", it) as GenderDto?,
                clientType = it.valueOf(clientType, ClientTypeDto.CLIENT),
                loginInfo = checkNullableField("loginInfo", it) as LoginInfoDto?,
            )
        }
    }

}