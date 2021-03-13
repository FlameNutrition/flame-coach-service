package com.coach.flame.jpa.entity

import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object CoachBuilder {

    fun maker(): Maker<Coach> {
        return an(CoachMaker.Coach)
    }

    fun default(): Coach {
        return maker().make()
    }

}
