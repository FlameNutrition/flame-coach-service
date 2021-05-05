package com.coach.flame.jpa.entity.maker

import com.coach.flame.jpa.entity.CountryConfig
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object CountryBuilder {

    private val MAKER: Maker<CountryConfig> = an(CountryMaker.CountryConfig)

    fun maker(): Maker<CountryConfig> {
        return MAKER
    }

    fun default(): CountryConfig {
        return maker().make()
    }

}
