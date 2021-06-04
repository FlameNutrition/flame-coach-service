package com.coach.flame.jpa.entity.maker

import com.coach.flame.jpa.entity.RegistrationInvite
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object RegistrationInviteBuilder {

    private val MAKER: Maker<RegistrationInvite> = an(RegistrationInviteMaker.RegistrationInvite)

    fun maker(): Maker<RegistrationInvite> {
        return MAKER
    }

    fun default(): RegistrationInvite {
        return maker().make()
    }

}
