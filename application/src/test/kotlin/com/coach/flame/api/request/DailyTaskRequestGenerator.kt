package com.coach.flame.api.request

import com.github.javafaker.Faker
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.FieldPredicates
import org.jeasy.random.api.Randomizer
import java.util.*

class DailyTaskRequestGenerator {

    data class Builder(
        private var randomizerName: Randomizer<String>? = null,
        private var randomizerDescription: Randomizer<String>? = null,
        private var randomizerDate: Randomizer<String>? = null,
        private var randomizerClientIdentifierTask: Randomizer<String>? = null,
        private var randomizerClientIdentifierCreator: Randomizer<String>? = null,
        private var easyRandom: EasyRandom? = null
    ) {

        private val faker = Faker()

        fun withName(randomizer: Randomizer<String>) = apply {
            this.randomizerName = randomizer
        }

        fun withDescription(randomizer: Randomizer<String>) = apply {
            this.randomizerDescription = randomizer
        }

        fun withDate(randomizer: Randomizer<String>) = apply {
            this.randomizerDate = randomizer
        }

        fun withClientIdentifierTask(randomizer: Randomizer<String>) = apply {
            this.randomizerClientIdentifierTask = randomizer
        }

        fun withClientIdentifierCreator(randomizer: Randomizer<String>) = apply {
            this.randomizerClientIdentifierCreator = randomizer
        }

        private val fieldPredicateName = FieldPredicates
            .named("description")
            .and(FieldPredicates.ofType(String::class.java))
            .and(FieldPredicates.inClass(DailyTaskRequest::class.java))

        private val fieldPredicateDescription = FieldPredicates
            .named("description")
            .and(FieldPredicates.ofType(String::class.java))
            .and(FieldPredicates.inClass(DailyTaskRequest::class.java))

        private val fieldPredicateDate = FieldPredicates
            .named("date")
            .and(FieldPredicates.ofType(String::class.java))
            .and(FieldPredicates.inClass(DailyTaskRequest::class.java))

        private val fieldPredicateClientIdentifierTask = FieldPredicates
            .named("clientIdentifierTask")
            .and(FieldPredicates.ofType(String::class.java))
            .and(FieldPredicates.inClass(DailyTaskRequest::class.java))

        private val fieldPredicateClientIdentifierCreator = FieldPredicates
            .named("clientIdentifierCreator")
            .and(FieldPredicates.ofType(String::class.java))
            .and(FieldPredicates.inClass(DailyTaskRequest::class.java))

        private val defaultRandomizerName = Randomizer { faker.food().dish() }
        private val defaultRandomizerDescription = Randomizer { faker.yoda().quote() }
        private val defaultRandomizerDate = Randomizer { faker.date().birthday() }
        private val defaultRandomizerClientIdentifierTask = Randomizer { UUID.randomUUID().toString() }
        private val defaultRandomizerClientIdentifierCreator = Randomizer { UUID.randomUUID().toString() }

        fun build() = apply {

            val randomParameter = EasyRandomParameters()
                .randomize(fieldPredicateName, randomizerName ?: defaultRandomizerName)
                .randomize(fieldPredicateDescription, randomizerDescription ?: defaultRandomizerDescription)
                .randomize(fieldPredicateDate, randomizerDate ?: defaultRandomizerDate)
                .randomize(
                    fieldPredicateClientIdentifierTask,
                    randomizerClientIdentifierTask ?: defaultRandomizerClientIdentifierTask
                )
                .randomize(
                    fieldPredicateClientIdentifierCreator,
                    randomizerClientIdentifierCreator ?: defaultRandomizerClientIdentifierCreator
                )
                .objectPoolSize(3)

            this.easyRandom = EasyRandom(randomParameter)
        }

        fun nextObject(): DailyTaskRequestGenerator {

            checkNotNull(this.easyRandom) { "please call first the build method!" }
            return this.easyRandom!!.nextObject(DailyTaskRequestGenerator::class.java)

        }

    }

}