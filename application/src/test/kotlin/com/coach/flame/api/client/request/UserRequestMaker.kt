package com.coach.flame.api.client.request

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty

class UserRequestMaker {

    companion object {

        private val fake = Faker()
        val FIRST_NAME: Property<ClientRequest, String?> = newProperty()
        val LASTNAME: Property<ClientRequest, String?> = newProperty()
        val EMAIL: Property<ClientRequest, String?> = newProperty()
        val PASSWORD: Property<ClientRequest, String?> = newProperty()
        val TYPE: Property<ClientRequest, String?> = newProperty()
        val POLICY: Property<ClientRequest, Boolean?> = newProperty()

        val ClientRequest: Instantiator<ClientRequest> = Instantiator {
            ClientRequest(
                firstname = it.valueOf(FIRST_NAME, fake.name().firstName()),
                lastname = it.valueOf(LASTNAME, fake.name().lastName()),
                email = it.valueOf(EMAIL, fake.internet().emailAddress()),
                password = it.valueOf(PASSWORD, fake.internet().password()),
                type = it.valueOf(TYPE, "CLIENT"),
                policy = it.valueOf(POLICY, null as Boolean?)
            )
        }
    }

}