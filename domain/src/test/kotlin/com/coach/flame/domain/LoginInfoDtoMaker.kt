package com.coach.flame.domain

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import com.natpryce.makeiteasy.PropertyLookup
import java.time.LocalDateTime
import java.util.*

class LoginInfoDtoMaker {

    companion object {

        private val fake = Faker()
        val username: Property<LoginInfoDto, String> = newProperty()
        val password: Property<LoginInfoDto, String> = newProperty()
        val expirationDate: Property<LoginInfoDto, LocalDateTime?> = newProperty()
        val token: Property<LoginInfoDto, UUID?> = newProperty()

        val LoginInfoDto: Instantiator<LoginInfoDto> = Instantiator {
            LoginInfoDto(
                username = it.valueOf(username, fake.internet().emailAddress()),
                password = it.valueOf(password, fake.internet().password()),
                expirationDate = it.valueOf(expirationDate, LocalDateTime.now()),
                token = it.valueOf(token, UUID.randomUUID())
            )
        }
    }

}