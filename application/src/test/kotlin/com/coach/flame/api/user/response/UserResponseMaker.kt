package com.coach.flame.api.user.response

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDateTime
import java.util.*

class UserResponseMaker {

    companion object {

        private val fake = Faker()
        val username: Property<UserResponse, String> = newProperty()
        val firstname: Property<UserResponse, String> = newProperty()
        val lastname: Property<UserResponse, String> = newProperty()
        val token: Property<UserResponse, UUID> = newProperty()
        val expiration: Property<UserResponse, LocalDateTime> = newProperty()

        val UserResponse: Instantiator<UserResponse> = Instantiator {
            UserResponse(
                username = it.valueOf(username, fake.internet().emailAddress()),
                firstname = it.valueOf(firstname, fake.name().firstName()),
                lastname = it.valueOf(lastname, fake.name().lastName()),
                token = it.valueOf(token, UUID.randomUUID()),
                expiration = it.valueOf(expiration, LocalDateTime.now()),
            )
        }
    }

}