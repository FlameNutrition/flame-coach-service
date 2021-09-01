package com.coach.flame.jpa.entity.maker

import com.coach.flame.jpa.entity.CountryConfig
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property

object CountryBuilder {

    private val MAKER: Maker<CountryConfig> = an(CountryMaker.CountryConfig)

    fun maker(): Maker<CountryConfig> {
        return MAKER
    }

    fun default(): CountryConfig {
        return maker().make()
    }

}

class CountryMaker {

    companion object {

        private val fake = Faker()
        val countryCode: Property<CountryConfig, String> = Property.newProperty()
        val externalValue: Property<CountryConfig, String> = Property.newProperty()

        val CountryConfig: Instantiator<CountryConfig> = Instantiator {
            CountryConfig(
                countryCode = it.valueOf(countryCode, fake.country().countryCode3()),
                externalValue = it.valueOf(externalValue, fake.country().name())
            )
        }
    }

}
