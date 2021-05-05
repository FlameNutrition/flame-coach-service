package com.coach.flame.jpa.entity.maker

import com.coach.flame.jpa.entity.ClientMeasureWeight
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object ClientMeasureWeightBuilder {

    private val MAKER: Maker<ClientMeasureWeight> = an(ClientMeasureWeightMaker.ClientMeasureWeight)

    fun maker(): Maker<ClientMeasureWeight> {
        return MAKER
    }

    fun default(): ClientMeasureWeight {
        return maker().make()
    }

}
