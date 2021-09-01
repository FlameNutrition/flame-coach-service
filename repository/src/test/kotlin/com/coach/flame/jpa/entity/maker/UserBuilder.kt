package com.coach.flame.jpa.entity.maker

import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.entity.User
import com.coach.flame.jpa.entity.UserSession
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property

object UserBuilder {

    private val MAKER: Maker<User> = an(UserMaker.User)

    fun maker(): Maker<User> {
        return MAKER
    }

    fun default(): User {
        return maker().make()
    }

}

class UserMaker {

    companion object {

        private val fake = Faker()
        val email: Property<User, String> = Property.newProperty()
        val password: Property<User, String> = Property.newProperty()
        val key: Property<User, String> = Property.newProperty()
        val client: Property<User, Client?> = Property.newProperty()
        val coach: Property<User, Coach?> = Property.newProperty()
        val userSession: Property<User, UserSession> = Property.newProperty()

        val User: Instantiator<User> = Instantiator {
            User(
                email = it.valueOf(email, fake.internet().emailAddress()),
                password = it.valueOf(password, fake.internet().password()),
                keyDecrypt = it.valueOf(key, "salt"),
                userSession = it.valueOf(userSession, MakeItEasy.make(MakeItEasy.a(UserSessionMaker.UserSession))),
                client = it.valueOf(client, null as Client?),
                coach = it.valueOf(coach, null as Coach?)
            )
        }
    }

}
