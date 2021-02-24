package com.coach.flame.jpa.entity

import com.github.javafaker.Faker
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.FieldPredicates
import org.jeasy.random.api.Randomizer
import kotlin.random.Random

class CountryConfigGenerator {

    data class Builder(
        private var randomizerId: Randomizer<Long>? = null,
        private var randomizerCountryCode: Randomizer<String>? = null,
        private var randomizerExternalValue: Randomizer<String>? = null,
        private var easyRandom: EasyRandom? = null
    ) {

        private val faker = Faker()

        private val fieldPredicateId = FieldPredicates
            .named("id")
            .and(FieldPredicates.ofType(Long::class.java))
            .and(FieldPredicates.inClass(CountryConfig::class.java))

        private val fieldPredicateCountryCode = FieldPredicates
            .named("countryCode")
            .and(FieldPredicates.ofType(String::class.java))
            .and(FieldPredicates.inClass(CountryConfig::class.java))

        private val fieldPredicateExternalValue = FieldPredicates
            .named("externalValue")
            .and(FieldPredicates.ofType(MutableList::class.java))
            .and(FieldPredicates.inClass(CountryConfig::class.java))

        private val defaultRandomizerId = Randomizer { Random.nextLong(1, 20000) }
        private val defaultRandomizerCountryCode = Randomizer { faker.country().countryCode3() }
        private val defaultRandomizerExternalValue = Randomizer { faker.country().name() }

        fun withRandomizerCountryCode(randomizer: Randomizer<String>) =
            apply { this.randomizerCountryCode = randomizer }

        fun withRandomizerExternalValue(randomizer: Randomizer<String>) =
            apply { this.randomizerExternalValue = randomizer }

        fun build() = apply {

            val randomParameter = EasyRandomParameters()
                .randomize(fieldPredicateId, randomizerId ?: defaultRandomizerId)
                .randomize(fieldPredicateCountryCode, randomizerCountryCode ?: defaultRandomizerCountryCode)
                .randomize(fieldPredicateExternalValue, randomizerExternalValue ?: defaultRandomizerExternalValue)
                .objectPoolSize(3)
                .scanClasspathForConcreteTypes(true)

            this.easyRandom = EasyRandom(randomParameter)
        }

        fun nextObject(): CountryConfig {

            checkNotNull(this.easyRandom) { "please call first the build method!" }
            return this.easyRandom!!.nextObject(CountryConfig::class.java)

        }

    }
}