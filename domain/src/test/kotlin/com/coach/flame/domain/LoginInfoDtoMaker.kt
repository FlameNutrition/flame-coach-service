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

        val nullableFields: Property<LoginInfoDto, List<String>> = newProperty()

        private fun checkNullableField(fieldName: String, it: PropertyLookup<LoginInfoDto>): Any? {

            if (it.valueOf(nullableFields, listOf()).contains(fieldName)) {
                return null
            }

            when (fieldName) {
                "expirationDate" -> return it.valueOf(expirationDate, LocalDateTime.now())
                "token" -> return it.valueOf(token, UUID.randomUUID())
            }

            return null
        }

        val LoginInfoDto: Instantiator<LoginInfoDto> = Instantiator {
            LoginInfoDto(
                username = it.valueOf(username, fake.internet().emailAddress()),
                password = it.valueOf(password, fake.internet().password()),
                expirationDate = checkNullableField("expirationDate", it) as LocalDateTime?,
                token = checkNullableField("token", it) as UUID?
            )
        }
    }

}