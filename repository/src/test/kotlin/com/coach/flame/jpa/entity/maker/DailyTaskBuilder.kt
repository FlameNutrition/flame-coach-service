package com.coach.flame.jpa.entity.maker

import com.coach.flame.jpa.entity.DailyTask
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object DailyTaskBuilder {

    private val MAKER: Maker<DailyTask> = an(DailyTaskMaker.DailyTask)

    fun maker(): Maker<DailyTask> {
        return MAKER
    }

    fun default(): DailyTask {
        return maker().make()
    }

}
