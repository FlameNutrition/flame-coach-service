package com.coach.flame.api.customer.response

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDateTime
import java.util.*

class CustomerResponseMaker {

    companion object {

        private val fake = Faker()
        val username: Property<CustomerResponse, String> = newProperty()
        val firstName: Property<CustomerResponse, String> = newProperty()
        val lastName: Property<CustomerResponse, String> = newProperty()
        val token: Property<CustomerResponse, UUID> = newProperty()
        val expiration: Property<CustomerResponse, LocalDateTime> = newProperty()
        val type: Property<CustomerResponse, String> = newProperty()
        val identifier: Property<CustomerResponse, UUID> = newProperty()

        val CustomerResponse: Instantiator<CustomerResponse> = Instantiator {
            CustomerResponse(
                username = it.valueOf(username, fake.internet().emailAddress()),
                firstname = it.valueOf(firstName, fake.name().firstName()),
                lastname = it.valueOf(lastName, fake.name().lastName()),
                token = it.valueOf(token, UUID.randomUUID()),
                expiration = it.valueOf(expiration, LocalDateTime.now()),
                type = it.valueOf(type, "UNDEFINED"),
                identifier = it.valueOf(identifier, UUID.randomUUID()),
            )
        }
    }

}