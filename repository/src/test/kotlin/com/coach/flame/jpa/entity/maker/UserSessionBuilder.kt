package com.coach.flame.jpa.entity.maker

import com.coach.flame.jpa.entity.UserSession
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object UserSessionBuilder {

    private val MAKER: Maker<UserSession> = an(UserSessionMaker.UserSession)

    fun maker(): Maker<UserSession> {
        return MAKER
    }

    fun default(): UserSession {
        return maker().make()
    }

}