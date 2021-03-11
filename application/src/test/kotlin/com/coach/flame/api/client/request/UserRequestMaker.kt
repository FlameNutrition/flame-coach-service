package com.coach.flame.api.client.request

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty

class UserRequestMaker {

    companion object {

        private val fake = Faker()
        val firstName: Property<ClientRequest, String?> = newProperty()
        val lastName: Property<ClientRequest, String?> = newProperty()
        val email: Property<ClientRequest, String?> = newProperty()
        val password: Property<ClientRequest, String?> = newProperty()
        val type: Property<ClientRequest, String?> = newProperty()
        val policy: Property<ClientRequest, Boolean?> = newProperty()

        val ClientRequest: Instantiator<ClientRequest> = Instantiator {
            ClientRequest(
                firstname = it.valueOf(firstName, fake.name().firstName()),
                lastname = it.valueOf(lastName, fake.name().lastName()),
                email = it.valueOf(email, fake.internet().emailAddress()),
                password = it.valueOf(password, fake.internet().password()),
                type = it.valueOf(type, "CLIENT"),
                policy = it.valueOf(policy, null as Boolean?)
            )
        }
    }

}