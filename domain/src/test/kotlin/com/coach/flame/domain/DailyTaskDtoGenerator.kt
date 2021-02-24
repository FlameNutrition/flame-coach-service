package com.coach.flame.domain

import com.github.javafaker.Faker
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.FieldPredicates
import org.jeasy.random.api.Randomizer

class DailyTaskDtoGenerator {

    data class Builder(
        private val randomizerName: Randomizer<String>? = null,
        private val randomizerDescription: Randomizer<String>? = null,
        private var easyRandom: EasyRandom? = null
    ) {

        private val faker = Faker()

        private val fieldPredicateName = FieldPredicates
            .named("name")
            .and(FieldPredicates.ofType(String::class.java))
            .and(FieldPredicates.inClass(DailyTaskDto::class.java))

        private val fieldPredicateDescription = FieldPredicates
            .named("description")
            .and(FieldPredicates.ofType(String::class.java))
            .and(FieldPredicates.inClass(DailyTaskDto::class.java))

        private val defaultRandomizerName = Randomizer { faker.harryPotter().character() }
        private val defaultRandomizerDescription = Randomizer { faker.yoda().quote() }

        fun build() = apply {

            val randomParameter = EasyRandomParameters()
                .randomize(fieldPredicateName, randomizerName ?: defaultRandomizerName)
                .randomize(fieldPredicateDescription, randomizerDescription ?: defaultRandomizerDescription)
                .objectPoolSize(3)
                .scanClasspathForConcreteTypes(true)

            this.easyRandom = EasyRandom(randomParameter)
        }

        fun nextObject(): DailyTaskDto {

            checkNotNull(this.easyRandom) { "please call first the build method!" }
            return this.easyRandom!!.nextObject(DailyTaskDto::class.java)

        }

    }
}