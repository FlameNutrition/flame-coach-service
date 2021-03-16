package com.coach.flame.jpa.entity

import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object GenderBuilder {

    private val MAKER: Maker<GenderConfig> = an(GenderMaker.GenderConfig)

    fun maker(): Maker<GenderConfig> {
        return MAKER
    }

    fun default(): GenderConfig {
        return maker().make()
    }

}
