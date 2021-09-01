package com.coach.flame.domain.maker

import com.coach.flame.domain.*
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.MakeItEasy.with
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

object ClientDtoBuilder {

    private val MAKER: Maker<ClientDto> = an(ClientDtoMaker.ClientDto)

    fun makerWithLoginInfo(): Maker<ClientDto> {
        val loginInfo = LoginInfoDtoBuilder.maker()
            .but(with(LoginInfoDtoMaker.expirationDate, LocalDateTime.now()),
                with(LoginInfoDtoMaker.token, UUID.randomUUID()))
            .make()

        return MAKER
            .but(with(ClientDtoMaker.loginInfo, loginInfo))
    }

    fun maker(): Maker<ClientDto> {
        return MAKER
    }

    fun default(): ClientDto {
        return maker().make()
    }

}

class ClientDtoMaker {

    companion object {

        private val fake = Faker()
        val id: Property<ClientDto, Long?> = Property.newProperty()
        val identifier: Property<ClientDto, UUID> = Property.newProperty()
        val firstName: Property<ClientDto, String> = Property.newProperty()
        val lastName: Property<ClientDto, String> = Property.newProperty()
        val birthday: Property<ClientDto, LocalDate?> = Property.newProperty()
        val phoneCode: Property<ClientDto, String?> = Property.newProperty()
        val phoneNumber: Property<ClientDto, String?> = Property.newProperty()
        val country: Property<ClientDto, CountryDto?> = Property.newProperty()
        val gender: Property<ClientDto, GenderDto?> = Property.newProperty()
        val customerType: Property<ClientDto, CustomerTypeDto> = Property.newProperty()
        val loginInfo: Property<ClientDto, LoginInfoDto?> = Property.newProperty()
        val clientStatus: Property<ClientDto, ClientStatusDto> = Property.newProperty()
        val registrationDate: Property<ClientDto, LocalDate> = Property.newProperty()
        val coach: Property<ClientDto, CoachDto?> = Property.newProperty()
        val weight: Property<ClientDto, Float> = Property.newProperty()
        val height: Property<ClientDto, Float> = Property.newProperty()
        val measureType: Property<ClientDto, MeasureTypeDto> = Property.newProperty()
        val listOfWeights: Property<ClientDto, MutableList<MeasureDto>> = Property.newProperty()
        val registrationKey: Property<ClientDto, String?> = Property.newProperty()

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
