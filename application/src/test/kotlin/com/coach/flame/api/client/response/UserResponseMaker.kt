package com.coach.flame.api.client.response

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDateTime
import java.util.*

class UserResponseMaker {

    companion object {

        private val fake = Faker()
        val USERNAME: Property<ClientResponse, String> = newProperty()
        val FIRSTNAME: Property<ClientResponse, String> = newProperty()
        val LASTNAME: Property<ClientResponse, String> = newProperty()
        val TOKEN: Property<ClientResponse, UUID> = newProperty()
        val EXPIRATION: Property<ClientResponse, LocalDateTime> = newProperty()

        val ClientResponse: Instantiator<ClientResponse> = Instantiator {
            ClientResponse(
                username = it.valueOf(USERNAME, fake.internet().emailAddress()),
                firstname = it.valueOf(FIRSTNAME, fake.name().firstName()),
                lastname = it.valueOf(LASTNAME, fake.name().lastName()),
                token = it.valueOf(TOKEN, UUID.randomUUID()),
                expiration = it.valueOf(EXPIRATION, LocalDateTime.now()),
            )
        }
    }

}