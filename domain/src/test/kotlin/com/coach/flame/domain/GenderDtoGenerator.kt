package com.coach.flame.domain

import com.github.javafaker.Faker
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.FieldPredicates
import org.jeasy.random.api.Randomizer
import kotlin.random.Random

class GenderDtoGenerator {

    data class Builder(
        private var randomizerGenderCode: Randomizer<String>? = null,
        private var randomizerExternalValue: Randomizer<String>? = null,
        private var easyRandom: EasyRandom? = null
    ) {

        private val faker = Faker()

        private val fieldPredicateGenderCode = FieldPredicates
            .named("genderCode")
            .and(FieldPredicates.ofType(String::class.java))
            .and(FieldPredicates.inClass(GenderDto::class.java))

        private val fieldPredicateExternalValue = FieldPredicates
            .named("externalValue")
            .and(FieldPredicates.ofType(MutableList::class.java))
            .and(FieldPredicates.inClass(GenderDto::class.java))

        private val defaultRandomizerGenderCode = Randomizer { faker.demographic().sex() }
        private val defaultRandomizerExternalValue = Randomizer { faker.demographic().sex() }

        fun withRandomizerGenderCode(randomizer: Randomizer<String>) =
            apply { this.randomizerGenderCode = randomizer }

        fun withRandomizerExternalValue(randomizer: Randomizer<String>) =
            apply { this.randomizerExternalValue = randomizer }

        fun build() = apply {

            val randomParameter = EasyRandomParameters()
                .randomize(fieldPredicateGenderCode, randomizerGenderCode ?: defaultRandomizerGenderCode)
                .randomize(fieldPredicateExternalValue, randomizerExternalValue ?: defaultRandomizerExternalValue)
                .objectPoolSize(3)
                .scanClasspathForConcreteTypes(true)

            this.easyRandom = EasyRandom(randomParameter)
        }

        fun nextObject(): GenderDto {

            checkNotNull(this.easyRandom) { "please call first the build method!" }
            return this.easyRandom!!.nextObject(GenderDto::class.java)

        }

    }
}