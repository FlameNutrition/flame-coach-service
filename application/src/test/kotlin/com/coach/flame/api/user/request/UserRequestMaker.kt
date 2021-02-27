package com.coach.flame.api.user.request

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import com.natpryce.makeiteasy.PropertyLookup

class UserRequestMaker {

    companion object {

        private val fake = Faker()
        val firstName: Property<UserRequest, String?> = newProperty()
        val lastname: Property<UserRequest, String?> = newProperty()
        val email: Property<UserRequest, String?> = newProperty()
        val password: Property<UserRequest, String?> = newProperty()
        val type: Property<UserRequest, String?> = newProperty()
        val policy: Property<UserRequest, Boolean> = newProperty()

        val UserRequest: Instantiator<UserRequest> = Instantiator {
            UserRequest(
                firstname = it.valueOf(firstName, fake.name().firstName()),
                lastname = it.valueOf(lastname, fake.name().lastName()),
                email = it.valueOf(email, fake.internet().emailAddress()),
                password = it.valueOf(password, fake.internet().password()),
                type = it.valueOf(type, "CLIENT"),
                policy = it.valueOf(policy, false)
            )
        }
    }

}