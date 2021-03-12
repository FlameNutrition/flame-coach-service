package com.coach.flame.api.coach.response

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.util.*

class ClientCoachMaker {

    companion object {

        private val fake = Faker()
        val firstName: Property<ClientCoach, String> = newProperty()
        val lastName: Property<ClientCoach, String> = newProperty()
        val identifier: Property<ClientCoach, UUID> = newProperty()

        val ClientCoach: Instantiator<ClientCoach> = Instantiator {
            ClientCoach(
                firstname = it.valueOf(firstName, fake.name().firstName()),
                lastname = it.valueOf(lastName, fake.name().lastName()),
                identifier = it.valueOf(identifier, UUID.randomUUID())
            )
        }
    }

}