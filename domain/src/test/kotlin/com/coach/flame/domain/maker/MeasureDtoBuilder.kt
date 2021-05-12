package com.coach.flame.domain.maker

import com.coach.flame.domain.MeasureDto
import com.natpryce.makeiteasy.MakeItEasy
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import java.util.concurrent.atomic.AtomicLong

object MeasureDtoBuilder {

    private val atomicId: AtomicLong = AtomicLong(1)

    private val MAKER: Maker<MeasureDto> = an(MeasureDtoMaker.MeasureDto)

    fun makerWithId(): Maker<MeasureDto> {
        return maker()
            .but(MakeItEasy.with(MeasureDtoMaker.id, atomicId.getAndIncrement()))
    }

    fun maker(): Maker<MeasureDto> {
        return MAKER
    }

    fun default(): MeasureDto {
        return maker().make()
    }

}
