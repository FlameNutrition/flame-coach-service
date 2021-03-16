package com.coach.flame.domain

import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object CoachDtoBuilder {

    private val MAKER: Maker<CoachDto> = an(CoachDtoMaker.CoachDto)

    fun maker(): Maker<CoachDto> {
        return MAKER
    }

    fun default(): CoachDto {
        return maker().make()
    }

}
