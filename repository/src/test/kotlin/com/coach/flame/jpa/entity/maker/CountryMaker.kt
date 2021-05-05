package com.coach.flame.jpa.entity.maker

import com.coach.flame.jpa.entity.CountryConfig
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty

class CountryMaker {

    companion object {

        private val fake = Faker()
        val countryCode: Property<CountryConfig, String> = newProperty()
        val externalValue: Property<CountryConfig, String> = newProperty()

        val CountryConfig: Instantiator<CountryConfig> = Instantiator {
            CountryConfig(
                countryCode = it.valueOf(countryCode, fake.country().countryCode3()),
                externalValue = it.valueOf(externalValue, fake.country().name())
            )
        }
    }

}
