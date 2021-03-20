package com.coach.flame.domain

import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object DailyTaskDtoBuilder {

    private val MAKER: Maker<DailyTaskDto> = an(DailyTaskDtoMaker.DailyTaskDto)

    fun maker(): Maker<DailyTaskDto> {
        return MAKER
    }

    fun default(): DailyTaskDto {
        return maker().make()
    }

}
