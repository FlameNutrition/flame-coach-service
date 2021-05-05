package com.coach.flame.domain.maker

import com.coach.flame.domain.LoginInfoDto
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object LoginInfoDtoBuilder {

    private val MAKER: Maker<LoginInfoDto> = an(LoginInfoDtoMaker.LoginInfoDto)

    fun maker(): Maker<LoginInfoDto> {
        return MAKER
    }

    fun default(): LoginInfoDto {
        return maker().make()
    }

}
