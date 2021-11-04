package com.coach.flame.jpa.entity.maker

import com.coach.flame.jpa.entity.*
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property
import java.time.LocalDate
import java.util.*

internal object ClientBuilder {

    private val MAKER: Maker<Client> = an(ClientMaker.Client)

    fun maker(): Maker<Client> {
        return MAKER
    }

    fun default(): Client {
        return maker().make()
    }

}

internal class ClientMaker {

    companion object {

        private val fake = Faker()
        val uuid: Property<Client, UUID> = Property.newProperty()
        val firstname: Property<Client, String> = Property.newProperty()
        val lastname: Property<Client, String> = Property.newProperty()
        val birthday: Property<Client, LocalDate?> = Property.newProperty()
        val phoneCode: Property<Client, String?> = Property.newProperty()
        val phoneNumber: Property<Client, String?> = Property.newProperty()
        val country: Property<Client, CountryConfig?> = Property.newProperty()
        val gender: Property<Client, GenderConfig?> = Property.newProperty()
        val user: Property<Client, User> = Property.newProperty()
        val clientType: Property<Client, ClientType> = Property.newProperty()
        val clientMeasureWeight: Property<Client, MutableList<ClientMeasureWeight>> = Property.newProperty()
        val dailyClientTask: Property<Client, MutableList<DailyTask>> = Property.newProperty()
        val coach: Property<Client, Coach> = Property.newProperty()
        val clientStatus: Property<Client, ClientStatus> = Property.newProperty()
        val registrationDate: Property<Client, LocalDate> = Property.newProperty()
        val measureConfig: Property<Client, MeasureConfig> = Property.newProperty()
        val weight: Property<Client, Float> = Property.newProperty()
        val height: Property<Client, Float> = Property.newProperty()

        val Client: Instantiator<Client> = Instantiator {

            val userSession = UserSessionBuilder.default()

            val userInit = UserBuilder.maker()
                .but(MakeItEasy.with(UserMaker.userSession, userSession))
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
                clientType = it.valueOf(clientType, MakeItEasy.make(MakeItEasy.a(ClientTypeMaker.ClientType))),
                clientMeasureWeight = it.valueOf(clientMeasureWeight, mutableListOf()),
                dailyClientTask = it.valueOf(dailyClientTask, mutableListOf()),
                coach = it.valueOf(coach, null as Coach?),
                height = it.valueOf(height, 0.0f),
                weight = it.valueOf(weight, 0.0f),
                clientStatus = it.valueOf(clientStatus, ClientStatus.AVAILABLE),
                measureConfig = it.valueOf(measureConfig, MeasureConfig.KG_CM),
                registrationDate = it.valueOf(registrationDate, LocalDate.now()),
                clientLookingForCoach = ClientLookingForCoach()
            )
        }
    }

}
