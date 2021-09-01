package com.coach.flame.domain.maker

import com.coach.flame.domain.CountryDto
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property

object CountryDtoBuilder {

    private val MAKER: Maker<CountryDto> = an(CountryDtoMaker.CountryDto)

    fun maker(): Maker<CountryDto> {
        return MAKER
    }

    fun default(): CountryDto {
        return maker().make()
    }

}

class CountryDtoMaker {

    companion object {

        private val fake = Faker()
        val id: Property<CountryDto, Long?> = Property.newProperty()
        val countryCode: Property<CountryDto, String> = Property.newProperty()
        val externalValue: Property<CountryDto, String> = Property.newProperty()

        val CountryDto: Instantiator<CountryDto> = Instantiator {
            CountryDto(
                id = it.valueOf(id, null as Long?),
                countryCode = it.valueOf(countryCode, fake.country().countryCode3()),
                externalValue = it.valueOf(externalValue, fake.country().name())
            )
        }
    }

}
