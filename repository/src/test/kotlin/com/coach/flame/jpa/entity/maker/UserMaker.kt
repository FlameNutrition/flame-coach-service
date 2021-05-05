package com.coach.flame.jpa.entity.maker

import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.entity.User
import com.coach.flame.jpa.entity.UserSession
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.a
import com.natpryce.makeiteasy.MakeItEasy.make
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty

class UserMaker {

    companion object {

        private val fake = Faker()
        val email: Property<User, String> = newProperty()
        val password: Property<User, String> = newProperty()
        val key: Property<User, String> = newProperty()
        val client: Property<User, Client?> = newProperty()
        val coach: Property<User, Coach?> = newProperty()
        val userSession: Property<User, UserSession> = newProperty()

        val User: Instantiator<User> = Instantiator {
            User(
                email = it.valueOf(email, fake.internet().emailAddress()),
                password = it.valueOf(password, fake.internet().password()),
                keyDecrypt = it.valueOf(key, "salt"),
                userSession = it.valueOf(userSession, make(a(UserSessionMaker.UserSession))),
                client = it.valueOf(client, null as Client?),
                coach = it.valueOf(coach, null as Coach?)
            )
        }
    }

}
