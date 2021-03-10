package com.coach.flame.jpa.entity

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy
import com.natpryce.makeiteasy.MakeItEasy.*
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDate
import java.util.*
import kotlin.random.Random

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
        val userSession: Property<Client, UserSession> = newProperty()
        val clientMeasureWeight: Property<Client, MutableList<ClientMeasureWeight>> = newProperty()
        val dailyClientTask: Property<Client, MutableList<DailyTask>> = newProperty()

        val Client: Instantiator<Client> = Instantiator {

            val userSession = make(a(UserSessionMaker.UserSession))

            val userInit = make(a(UserMaker.User,
                with(UserMaker.userSession, userSession)))

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
            )
        }
    }

}