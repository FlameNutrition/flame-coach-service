package com.coach.flame.domain

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDateTime
import java.util.*

class LoginInfoDtoMaker {

    companion object {

        private val fake = Faker()
        val username: Property<LoginInfoDto, String> = newProperty()
        val password: Property<LoginInfoDto, String> = newProperty()
        val keyDecrypt: Property<LoginInfoDto, String> = newProperty()
        val expirationDate: Property<LoginInfoDto, LocalDateTime?> = newProperty()
        val token: Property<LoginInfoDto, UUID?> = newProperty()

        val LoginInfoDto: Instantiator<LoginInfoDto> = Instantiator {
            LoginInfoDto(
                username = it.valueOf(username, fake.internet().emailAddress()),
                password = it.valueOf(password, fake.internet().password()),
                keyDecrypt = it.valueOf(keyDecrypt, "MY_SALT"),
                expirationDate = it.valueOf(expirationDate, null as LocalDateTime?),
                token = it.valueOf(token, null as UUID?)
            )
        }
    }

}
