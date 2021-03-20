package com.coach.flame.api.dailyTask.request

import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.MakeItEasy.listOf
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty

object DailyTaskFilterBuilder {

    private val MAKER: Maker<DailyTaskFilter> = an(DailyTaskFilterMaker.DailyTaskFilter)

    fun maker(): Maker<DailyTaskFilter> {
        return MAKER
    }

    fun default(): DailyTaskFilter {
        return maker().make()
    }

}

class DailyTaskFilterMaker {

    companion object {

        val type: Property<DailyTaskFilter, String> = newProperty()
        val values: Property<DailyTaskFilter, List<String>> = newProperty()

        val DailyTaskFilter: Instantiator<DailyTaskFilter> = Instantiator {
            DailyTaskFilter(
                type = it.valueOf(type, "INVALID"),
                values = it.valueOf(values, listOf()),
            )
        }
    }

}