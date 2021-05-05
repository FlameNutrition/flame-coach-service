package com.coach.flame.domain.maker

import com.coach.flame.domain.LoginInfoDto
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDateTime
import java.util.*

class LoginInfoDtoMaker {

    companion object {

        private val fake = Faker()
        val userId: Property<LoginInfoDto, Long?> = newProperty()
        val sessionId: Property<LoginInfoDto, Long?> = newProperty()
        val username: Property<LoginInfoDto, String> = newProperty()
        val password: Property<LoginInfoDto, String> = newProperty()
        val keyDecrypt: Property<LoginInfoDto, String> = newProperty()
        val expirationDate: Property<LoginInfoDto, LocalDateTime?> = newProperty()
        val token: Property<LoginInfoDto, UUID?> = newProperty()

        val LoginInfoDto: Instantiator<LoginInfoDto> = Instantiator {
            LoginInfoDto(
                userId = it.valueOf(userId, null as Long?),
                sessionId = it.valueOf(sessionId, null as Long?),
                username = it.valueOf(username, fake.internet().emailAddress()),
                password = it.valueOf(password, fake.internet().password()),
                keyDecrypt = it.valueOf(keyDecrypt, "MY_SALT"),
                expirationDate = it.valueOf(expirationDate, null as LocalDateTime?),
                token = it.valueOf(token, null as UUID?)
            )
        }
    }

}
