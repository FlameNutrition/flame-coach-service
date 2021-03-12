package com.coach.flame.domain

import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object CoachDtoBuilder {

    fun maker(): Maker<CoachDto> {
        return an(CoachDtoMaker.CoachDto)
    }

    fun default(): CoachDto {
        return maker().make()
    }

}
