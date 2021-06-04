package com.coach.flame.domain.maker

import com.coach.flame.domain.RegistrationInviteDto
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object RegistrationEmailDtoBuilder {

    private val MAKER: Maker<RegistrationInviteDto> = an(RegistrationEmailDtoMaker.RegistrationInviteDto)

    fun maker(): Maker<RegistrationInviteDto> {
        return MAKER
    }

    fun default(): RegistrationInviteDto {
        return maker().make()
    }

}
