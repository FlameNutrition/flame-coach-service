package com.coach.flame.jpa.entity

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty

class UserMaker {

    companion object {

        private val fake = Faker()
        val email: Property<User, String> = newProperty()
        val password: Property<User, String> = newProperty()
        val client: Property<User, Client> = newProperty()

        val User: Instantiator<User> = Instantiator {
            User(
                email = it.valueOf(email, fake.internet().emailAddress()),
                password = it.valueOf(password, fake.internet().password()),
                client = it.valueOf(client, null as Client?)
            )
        }
    }

}