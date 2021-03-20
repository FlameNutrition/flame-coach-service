package com.coach.flame.api.dailyTask.request

import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty

object DailyTaskFiltersRequestBuilder {

    private val MAKER: Maker<DailyTaskFiltersRequest> =
        MakeItEasy.an(DailyTaskFiltersRequestMaker.DailyTaskFiltersRequest)

    fun maker(): Maker<DailyTaskFiltersRequest> {
        return MAKER
    }

    fun default(): DailyTaskFiltersRequest {
        return maker().make()
    }

}

class DailyTaskFiltersRequestMaker {

    companion object {

        val filters: Property<DailyTaskFiltersRequest, Set<DailyTaskFilter>> = newProperty()

        val DailyTaskFiltersRequest: Instantiator<DailyTaskFiltersRequest> = Instantiator {
            DailyTaskFiltersRequest(
                filters = it.valueOf(filters, setOf())
            )
        }
    }

}