package com.coach.flame.jpa.entity

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.*
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDate
import java.util.*

class ClientMaker {

    companion object {

        private val fake = Faker()
        val uuid: Property<Client, UUID> = newProperty()
        val firstname: Property<Client, String> = newProperty()
        val lastname: Property<Client, String> = newProperty()
        val birthday: Property<Client, LocalDate?> = newProperty()
        val phoneCode: Property<Client, String?> = newProperty()
        val phoneNumber: Property<Client, String?> = newProperty()
        val country: Property<Client, CountryConfig?> = newProperty()
        val gender: Property<Client, GenderConfig?> = newProperty()
        val user: Property<Client, User> = newProperty()
        val clientType: Property<Client, ClientType> = newProperty()
        val clientMeasureWeight: Property<Client, MutableList<ClientMeasureWeight>> = newProperty()
        val dailyClientTask: Property<Client, MutableList<DailyTask>> = newProperty()
        val coach: Property<Client, Coach> = newProperty()
        val clientStatus: Property<Client, ClientStatus> = newProperty()
        val registrationDate: Property<Client, LocalDate> = newProperty()
        val measureConfig: Property<Client, MeasureConfig> = newProperty()
        val weight: Property<Client, Float> = newProperty()
        val height: Property<Client, Float> = newProperty()

        val Client: Instantiator<Client> = Instantiator {

            val userSession = UserSessionBuilder.default()

            val userInit = UserBuilder.maker()
                .but(with(UserMaker.userSession, userSession))
                .make()

            Client(
                uuid = it.valueOf(uuid, UUID.randomUUID()),
                firstName = it.valueOf(firstname, fake.name().firstName()),
                lastName = it.valueOf(lastname, fake.name().lastName()),
                birthday = it.valueOf(birthday, null as LocalDate?),
                phoneCode = it.valueOf(phoneCode, null as String?),
                phoneNumber = it.valueOf(phoneNumber, null as String?),
                country = it.valueOf(country, null as CountryConfig?),
                gender = it.valueOf(gender, null as GenderConfig?),
                user = it.valueOf(user, userInit),
                clientType = it.valueOf(clientType, make(a(ClientTypeMaker.ClientType))),
                clientMeasureWeight = it.valueOf(clientMeasureWeight, mutableListOf()),
                dailyClientTask = it.valueOf(dailyClientTask, mutableListOf()),
                coach = it.valueOf(coach, null as Coach?),
                height = it.valueOf(height, 0.0f),
                weight = it.valueOf(weight, 0.0f),
                clientStatus = it.valueOf(clientStatus, ClientStatus.AVAILABLE),
                measureConfig = it.valueOf(measureConfig, MeasureConfig.KG_CM),
                registrationDate = it.valueOf(registrationDate, LocalDate.now())
            )
        }
    }

}
