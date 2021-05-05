package com.coach.flame.jpa.entity.maker

import com.coach.flame.jpa.entity.User
import com.coach.flame.jpa.entity.UserSession
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDateTime
import java.util.*

class UserSessionMaker {

    companion object {

        val token: Property<UserSession, UUID> = newProperty()
        val expirationDate: Property<UserSession, LocalDateTime> = newProperty()
        val user: Property<UserSession, User?> = newProperty()

        val UserSession: Instantiator<UserSession> = Instantiator {
            UserSession(
                token = it.valueOf(token, UUID.randomUUID()),
                expirationDate = it.valueOf(expirationDate, LocalDateTime.now()),
                user = it.valueOf(user, null as User?)
            )
        }
    }

}
