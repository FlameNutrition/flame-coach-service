package com.coach.flame.domain

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import com.natpryce.makeiteasy.PropertyLookup
import java.util.*

class CountryDtoMaker {

    companion object {

        private val fake = Faker()
        val countryCode: Property<CountryDto, String> = newProperty()
        val externalValue: Property<CountryDto, String> = newProperty()

        val CountryDto: Instantiator<CountryDto> = Instantiator {
            CountryDto(
                countryCode = it.valueOf(countryCode, fake.country().countryCode3()),
                externalValue = it.valueOf(externalValue, fake.country().name())
            )
        }
    }

}