package com.coach.flame.jpa.entity

import com.github.javafaker.Faker
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.FieldPredicates
import org.jeasy.random.api.Randomizer
import kotlin.random.Random

class DailyTaskGenerator {

    data class Builder(
        private var randomizerId: Randomizer<Long>? = null,
        private var randomizerName: Randomizer<String>? = null,
        private var randomizerDescription: Randomizer<String>? = null,
        private var easyRandom: EasyRandom? = null
    ) {

        private val faker = Faker()

        private val fieldPredicateId = FieldPredicates
            .named("id")
            .and(FieldPredicates.ofType(Long::class.java))
            .and(FieldPredicates.inClass(DailyTask::class.java))

        private val fieldPredicateName = FieldPredicates
            .named("name")
            .and(FieldPredicates.ofType(String::class.java))
            .and(FieldPredicates.inClass(DailyTask::class.java))

        private val fieldPredicateClients = FieldPredicates
            .named("description")
            .and(FieldPredicates.ofType(String::class.java))
            .and(FieldPredicates.inClass(DailyTask::class.java))

        private val defaultRandomizerId = Randomizer { Random.nextLong(1, 20000) }
        private val defaultRandomizerName = Randomizer { faker.book().genre() }
        private val defaultRandomizerDescription = Randomizer { faker.yoda().quote() }

        fun build() = apply {

            val randomParameter = EasyRandomParameters()
                .randomize(fieldPredicateId, randomizerId ?: defaultRandomizerId)
                .randomize(fieldPredicateName, randomizerName ?: defaultRandomizerName)
                .randomize(fieldPredicateClients, randomizerDescription ?: defaultRandomizerDescription)
                .objectPoolSize(3)
                .scanClasspathForConcreteTypes(true)

            this.easyRandom = EasyRandom(randomParameter)
        }

        fun nextObject(): DailyTask {

            checkNotNull(this.easyRandom) { "please call first the build method!" }
            return this.easyRandom!!.nextObject(DailyTask::class.java)

        }

    }
}