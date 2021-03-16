package com.coach.flame.domain

import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object CountryDtoBuilder {

    private val MAKER: Maker<CountryDto> = an(CountryDtoMaker.CountryDto)

    fun maker(): Maker<CountryDto> {
        return MAKER
    }

    fun default(): CountryDto {
        return maker().make()
    }

}
