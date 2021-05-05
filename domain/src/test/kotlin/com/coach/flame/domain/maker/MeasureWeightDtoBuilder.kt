package com.coach.flame.domain.maker

import com.coach.flame.domain.MeasureWeightDto
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object MeasureWeightDtoBuilder {

    private val MAKER: Maker<MeasureWeightDto> = an(MeasureWeightDtoMaker.MeasureWeightDto)

    fun maker(): Maker<MeasureWeightDto> {
        return MAKER
    }

    fun default(): MeasureWeightDto {
        return maker().make()
    }

}
