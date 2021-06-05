package com.coach.flame.domain.maker

import com.coach.flame.domain.*
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDate
import java.util.*

class ClientDtoMaker {

    companion object {

        private val fake = Faker()
        val id: Property<ClientDto, Long?> = newProperty()
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
        val registrationDate: Property<ClientDto, LocalDate> = newProperty()
        val coach: Property<ClientDto, CoachDto?> = newProperty()
        val weight: Property<ClientDto, Float> = newProperty()
        val height: Property<ClientDto, Float> = newProperty()
        val measureType: Property<ClientDto, MeasureTypeDto> = newProperty()
        val listOfWeights: Property<ClientDto, MutableList<MeasureDto>> = newProperty()
        val registrationKey: Property<ClientDto, String?> = newProperty()

        val ClientDto: Instantiator<ClientDto> = Instantiator {
            ClientDto(
                id = it.valueOf(id, null as Long?),
                identifier = it.valueOf(identifier, UUID.randomUUID()),
                firstName = it.valueOf(firstName, fake.name().firstName()),
                lastName = it.valueOf(lastName, fake.name().lastName()),
                phoneNumber = it.valueOf(phoneNumber, null as String?),
                birthday = it.valueOf(birthday, null as LocalDate?),
                phoneCode = it.valueOf(phoneCode, null as String?),
                country = it.valueOf(country, null as CountryDto?),
                gender = it.valueOf(gender, null as GenderDto?),
                customerType = it.valueOf(customerType, CustomerTypeDto.CLIENT),
                loginInfo = it.valueOf(loginInfo, null as LoginInfoDto?),
                clientStatus = it.valueOf(clientStatus, ClientStatusDto.AVAILABLE),
                registrationDate = it.valueOf(registrationDate, LocalDate.now()),
                coach = it.valueOf(coach, null as CoachDto?),
                weight = it.valueOf(weight, 0.0f),
                height = it.valueOf(height, 0.0f),
                measureType = it.valueOf(measureType, MeasureTypeDto.KG_CM),
                weightMeasureTimeline = it.valueOf(listOfWeights, mutableListOf()),
                registrationKey = it.valueOf(registrationKey, null as String?)
            )
        }
    }

}
