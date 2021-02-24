package com.coach.flame.domain

import com.github.javafaker.Faker
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.FieldPredicates
import org.jeasy.random.api.Randomizer
import kotlin.random.Random

class CountryDtoGenerator {

    data class Builder(
        private var randomizerCountryCode: Randomizer<String>? = null,
        private var randomizerExternalValue: Randomizer<String>? = null,
        private var easyRandom: EasyRandom? = null
    ) {

        private val faker = Faker()

        private val fieldPredicateCountryCode = FieldPredicates
            .named("countryCode")
            .and(FieldPredicates.ofType(String::class.java))
            .and(FieldPredicates.inClass(CountryDto::class.java))

        private val fieldPredicateExternalValue = FieldPredicates
            .named("externalValue")
            .and(FieldPredicates.ofType(MutableList::class.java))
            .and(FieldPredicates.inClass(CountryDto::class.java))

        private val defaultRandomizerCountryCode = Randomizer { faker.country().countryCode3() }
        private val defaultRandomizerExternalValue = Randomizer { faker.country().name() }

        fun withRandomizerCountryCode(randomizer: Randomizer<String>) =
            apply { this.randomizerCountryCode = randomizer }

        fun withRandomizerExternalValue(randomizer: Randomizer<String>) =
            apply { this.randomizerExternalValue = randomizer }

        fun build() = apply {

            val randomParameter = EasyRandomParameters()
                .randomize(fieldPredicateCountryCode, randomizerCountryCode ?: defaultRandomizerCountryCode)
                .randomize(fieldPredicateExternalValue, randomizerExternalValue ?: defaultRandomizerExternalValue)
                .objectPoolSize(3)
                .scanClasspathForConcreteTypes(true)

            this.easyRandom = EasyRandom(randomParameter)
        }

        fun nextObject(): CountryDto {

            checkNotNull(this.easyRandom) { "please call first the build method!" }
            return this.easyRandom!!.nextObject(CountryDto::class.java)

        }

    }
}