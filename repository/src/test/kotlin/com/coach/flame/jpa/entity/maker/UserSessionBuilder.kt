package com.coach.flame.jpa.entity.maker

import com.coach.flame.jpa.entity.User
import com.coach.flame.jpa.entity.UserSession
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property
import java.time.LocalDateTime
import java.util.*

object UserSessionBuilder {

    private val MAKER: Maker<UserSession> = an(UserSessionMaker.UserSession)

    fun maker(): Maker<UserSession> {
        return MAKER
    }

    fun default(): UserSession {
        return maker().make()
    }

}

class UserSessionMaker {

    companion object {

        val token: Property<UserSession, UUID> = Property.newProperty()
        val expirationDate: Property<UserSession, LocalDateTime> = Property.newProperty()
        val user: Property<UserSession, User?> = Property.newProperty()

        val UserSession: Instantiator<UserSession> = Instantiator {
            UserSession(
                token = it.valueOf(token, UUID.randomUUID()),
                expirationDate = it.valueOf(expirationDate, LocalDateTime.now()),
                user = it.valueOf(user, null as User?)
            )
        }
    }

}
