package com.coach.flame.domain.maker

import com.coach.flame.domain.CountryDto
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty

class CountryDtoMaker {

    companion object {

        private val fake = Faker()
        val id: Property<CountryDto, Long?> = newProperty()
        val countryCode: Property<CountryDto, String> = newProperty()
        val externalValue: Property<CountryDto, String> = newProperty()

        val CountryDto: Instantiator<CountryDto> = Instantiator {
            CountryDto(
                id = it.valueOf(id, null as Long?),
                countryCode = it.valueOf(countryCode, fake.country().countryCode3()),
                externalValue = it.valueOf(externalValue, fake.country().name())
            )
        }
    }

}
