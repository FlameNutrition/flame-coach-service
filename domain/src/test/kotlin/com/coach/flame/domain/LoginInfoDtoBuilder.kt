package com.coach.flame.domain

import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object LoginInfoDtoBuilder {

    fun maker(): Maker<LoginInfoDto> {
        return an(LoginInfoDtoMaker.LoginInfoDto)
    }

    fun default(): LoginInfoDto {
        return maker().make()
    }

}
