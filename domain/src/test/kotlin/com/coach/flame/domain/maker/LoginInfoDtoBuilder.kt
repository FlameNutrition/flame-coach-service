package com.coach.flame.domain.maker

import com.coach.flame.domain.LoginInfoDto
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property
import java.time.LocalDateTime
import java.util.*

object LoginInfoDtoBuilder {

    private val MAKER: Maker<LoginInfoDto> = an(LoginInfoDtoMaker.LoginInfoDto)

    fun maker(): Maker<LoginInfoDto> {
        return MAKER
    }

    fun default(): LoginInfoDto {
        return maker().make()
    }

}

class LoginInfoDtoMaker {

    companion object {

        private val fake = Faker()
        val userId: Property<LoginInfoDto, Long?> = Property.newProperty()
        val sessionId: Property<LoginInfoDto, Long?> = Property.newProperty()
        val username: Property<LoginInfoDto, String> = Property.newProperty()
        val password: Property<LoginInfoDto, String> = Property.newProperty()
        val keyDecrypt: Property<LoginInfoDto, String> = Property.newProperty()
        val expirationDate: Property<LoginInfoDto, LocalDateTime?> = Property.newProperty()
        val token: Property<LoginInfoDto, UUID?> = Property.newProperty()

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
