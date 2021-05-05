package com.coach.flame.domain.maker

import com.coach.flame.domain.DailyTaskDto
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
