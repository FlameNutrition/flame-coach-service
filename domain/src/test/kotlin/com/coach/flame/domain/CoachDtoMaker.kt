package com.coach.flame.domain

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDate
import java.util.*

class CoachDtoMaker {

    companion object {

        private val fake = Faker()
        val identifier: Property<CoachDto, UUID> = newProperty()
        val firstName: Property<CoachDto, String> = newProperty()
        val lastName: Property<CoachDto, String> = newProperty()
        val birthday: Property<CoachDto, LocalDate?> = newProperty()
        val phoneCode: Property<CoachDto, String?> = newProperty()
        val phoneNumber: Property<CoachDto, String?> = newProperty()
        val country: Property<CoachDto, CountryDto?> = newProperty()
        val gender: Property<CoachDto, GenderDto?> = newProperty()
        val customerType: Property<CoachDto, CustomerTypeDto> = newProperty()
        val loginInfo: Property<CoachDto, LoginInfoDto?> = newProperty()
        val listOfClients: Property<CoachDto, Set<ClientDto>> = newProperty()
        val registrationDate: Property<CoachDto, LocalDate> = newProperty()

        val CoachDto: Instantiator<CoachDto> = Instantiator {
            CoachDto(
                identifier = it.valueOf(identifier, UUID.randomUUID()),
                firstName = it.valueOf(firstName, fake.name().firstName()),
                lastName = it.valueOf(lastName, fake.name().lastName()),
                birthday = it.valueOf(birthday, null as LocalDate?),
                phoneCode = it.valueOf(phoneCode, null as String?),
                phoneNumber = it.valueOf(phoneNumber, null as String?),
                country = it.valueOf(country, null as CountryDto?),
                gender = it.valueOf(gender, null as GenderDto?),
                customerType = it.valueOf(customerType, CustomerTypeDto.COACH),
                loginInfo = it.valueOf(loginInfo, null as LoginInfoDto?),
                listOfClients = it.valueOf(listOfClients, setOf()),
                registrationDate = it.valueOf(registrationDate, LocalDate.now())
            )
        }
    }

}
