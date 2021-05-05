package com.coach.flame.jpa.entity.maker

import com.coach.flame.jpa.entity.Coach
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object CoachBuilder {

    private val MAKER: Maker<Coach> = an(CoachMaker.Coach)

    fun maker(): Maker<Coach> {
        return MAKER
    }

    fun default(): Coach {
        return maker().make()
    }

}
