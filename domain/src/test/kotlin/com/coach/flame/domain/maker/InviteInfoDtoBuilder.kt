package com.coach.flame.domain.maker

import com.coach.flame.domain.InviteInfoDto
import com.coach.flame.domain.LoginInfoDto
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object InviteInfoDtoBuilder {

    private val MAKER: Maker<InviteInfoDto> = an(InviteInfoDtoMaker.InviteInfoDto)

    fun maker(): Maker<InviteInfoDto> {
        return MAKER
    }

    fun default(): InviteInfoDto {
        return maker().make()
    }

}
