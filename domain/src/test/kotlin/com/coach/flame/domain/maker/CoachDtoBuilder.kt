package com.coach.flame.domain.maker

import com.coach.flame.domain.*
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

object CoachDtoBuilder {

    private val MAKER: Maker<CoachDto> = an(CoachDtoMaker.CoachDto)

    fun makerWithLoginInfo(): Maker<CoachDto> {
        val loginInfo = LoginInfoDtoBuilder.maker()
            .but(MakeItEasy.with(LoginInfoDtoMaker.expirationDate, LocalDateTime.now()),
                MakeItEasy.with(LoginInfoDtoMaker.token, UUID.randomUUID()))
            .make()

        return MAKER
            .but(MakeItEasy.with(CoachDtoMaker.loginInfo, loginInfo))
    }

    fun maker(): Maker<CoachDto> {
        return MAKER
    }

    fun default(): CoachDto {
        return maker().make()
    }

}

class CoachDtoMaker {

    companion object {

        private val fake = Faker()
        val id: Property<CoachDto, Long?> = Property.newProperty()
        val identifier: Property<CoachDto, UUID> = Property.newProperty()
        val firstName: Property<CoachDto, String> = Property.newProperty()
        val lastName: Property<CoachDto, String> = Property.newProperty()
        val birthday: Property<CoachDto, LocalDate?> = Property.newProperty()
        val phoneCode: Property<CoachDto, String?> = Property.newProperty()
        val phoneNumber: Property<CoachDto, String?> = Property.newProperty()
        val country: Property<CoachDto, CountryDto?> = Property.newProperty()
        val gender: Property<CoachDto, GenderDto?> = Property.newProperty()
        val customerType: Property<CoachDto, CustomerTypeDto> = Property.newProperty()
        val loginInfo: Property<CoachDto, LoginInfoDto?> = Property.newProperty()
        val listOfClients: Property<CoachDto, Set<ClientDto>> = Property.newProperty()
        val registrationDate: Property<CoachDto, LocalDate> = Property.newProperty()

        val CoachDto: Instantiator<CoachDto> = Instantiator {
            CoachDto(
                id = it.valueOf(id, null as Long?),
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
