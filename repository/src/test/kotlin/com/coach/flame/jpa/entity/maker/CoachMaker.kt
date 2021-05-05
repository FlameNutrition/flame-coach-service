package com.coach.flame.jpa.entity.maker

import com.coach.flame.jpa.entity.*
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.*
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDate
import java.util.*

class CoachMaker {

    companion object {

        private val fake = Faker()
        val uuid: Property<Coach, UUID> = newProperty()
        val firstname: Property<Coach, String> = newProperty()
        val lastname: Property<Coach, String> = newProperty()
        val birthday: Property<Coach, LocalDate?> = newProperty()
        val phoneCode: Property<Coach, String?> = newProperty()
        val phoneNumber: Property<Coach, String?> = newProperty()
        val country: Property<Coach, CountryConfig?> = newProperty()
        val gender: Property<Coach, GenderConfig?> = newProperty()
        val user: Property<Coach, User> = newProperty()
        val clientType: Property<Coach, ClientType> = newProperty()
        val userSession: Property<Coach, UserSession> = newProperty()
        val clients: Property<Coach, List<Client>> = newProperty()
        val registrationDate: Property<Coach, LocalDate> = newProperty()

        val Coach: Instantiator<Coach> = Instantiator {

            val userSession = make(a(UserSessionMaker.UserSession))

            val userInit = make(a(UserMaker.User,
                with(UserMaker.userSession, userSession)))

            Coach(
                uuid = it.valueOf(uuid, UUID.randomUUID()),
                firstName = it.valueOf(firstname, fake.name().firstName()),
                lastName = it.valueOf(lastname, fake.name().lastName()),
                birthday = it.valueOf(birthday, null as LocalDate?),
                phoneCode = it.valueOf(phoneCode, null as String?),
                phoneNumber = it.valueOf(phoneNumber, null as String?),
                country = it.valueOf(country, null as CountryConfig?),
                gender = it.valueOf(gender, null as GenderConfig?),
                user = it.valueOf(user, userInit),
                clientType = it.valueOf(clientType, make(a(ClientTypeMaker.ClientType,
                    with(ClientTypeMaker.type, "COACH")))),
                clients = it.valueOf(clients, listOf()),
                registrationDate = it.valueOf(registrationDate, LocalDate.now())
            )
        }
    }

}
