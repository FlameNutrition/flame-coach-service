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
        val username: Property<ClientResponse, String> = newProperty()
        val firstName: Property<ClientResponse, String> = newProperty()
        val lastName: Property<ClientResponse, String> = newProperty()
        val token: Property<ClientResponse, UUID> = newProperty()
        val expiration: Property<ClientResponse, LocalDateTime> = newProperty()
        val type: Property<ClientResponse, String> = newProperty()

        val ClientResponse: Instantiator<ClientResponse> = Instantiator {
            ClientResponse(
                username = it.valueOf(username, fake.internet().emailAddress()),
                firstname = it.valueOf(firstName, fake.name().firstName()),
                lastname = it.valueOf(lastName, fake.name().lastName()),
                token = it.valueOf(token, UUID.randomUUID()),
                expiration = it.valueOf(expiration, LocalDateTime.now()),
                type = it.valueOf(type, "UNDEFINED")
            )
        }
    }

}