package com.coach.flame.jpa.entity

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.a
import com.natpryce.makeiteasy.MakeItEasy.make
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
            Client(
                uuid = it.valueOf(uuid, UUID.randomUUID()),
                firstName = it.valueOf(firstname, fake.name().firstName()),
                lastName = it.valueOf(lastname, fake.name().lastName()),
                birthday = it.valueOf(birthday, LocalDate.now()),
                phoneCode = it.valueOf(phoneCode, fake.phoneNumber().extension()),
                phoneNumber = it.valueOf(phoneNumber, fake.phoneNumber().phoneNumber()),
                country = it.valueOf(country, make(a(CountryMaker.CountryConfig))),
                gender = it.valueOf(gender, make(a(GenderMaker.GenderConfig))),
                user = it.valueOf(user, make(a(UserMaker.User))),
                userSession = it.valueOf(userSession, make(a(UserSessionMaker.UserSession))),
                clientType = it.valueOf(clientType, make(a(ClientTypeMaker.ClientType))),
                clientMeasureWeight = it.valueOf(clientMeasureWeight, mutableListOf()),
                dailyClientTask = it.valueOf(dailyClientTask, mutableListOf()),
            )
        }
    }

}