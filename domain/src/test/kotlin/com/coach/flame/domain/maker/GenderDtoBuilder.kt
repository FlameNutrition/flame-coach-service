package com.coach.flame.domain.maker

import com.coach.flame.domain.GenderDto
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object GenderDtoBuilder {

    private val MAKER: Maker<GenderDto> = an(GenderDtoMaker.GenderDto)

    fun maker(): Maker<GenderDto> {
        return MAKER
    }

    fun default(): GenderDto {
        return maker().make()
    }

}
