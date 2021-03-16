package com.coach.flame.jpa.entity

import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object UserBuilder {

    private val MAKER: Maker<User> = an(UserMaker.User)

    fun maker(): Maker<User> {
        return MAKER
    }

    fun default(): User {
        return maker().make()
    }

}
