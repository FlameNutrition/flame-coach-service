package com.coach.flame.api.customer.request

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty

class CustomerRequestMaker {

    companion object {

        private val fake = Faker()
        val firstName: Property<CustomerRequest, String?> = newProperty()
        val lastName: Property<CustomerRequest, String?> = newProperty()
        val email: Property<CustomerRequest, String?> = newProperty()
        val password: Property<CustomerRequest, String?> = newProperty()
        val type: Property<CustomerRequest, String?> = newProperty()
        val policy: Property<CustomerRequest, Boolean?> = newProperty()

        val CustomerRequest: Instantiator<CustomerRequest> = Instantiator {
            CustomerRequest(
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