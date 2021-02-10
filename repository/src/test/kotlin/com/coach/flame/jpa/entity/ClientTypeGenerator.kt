package com.coach.flame.jpa.entity

import com.github.javafaker.Faker
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.FieldPredicates
import org.jeasy.random.api.Randomizer
import kotlin.random.Random

class ClientTypeGenerator {

    data class Builder(
        private val randomizerId: Randomizer<Long>? = null,
        private val randomizerName: Randomizer<String>? = null,
        private val randomizerDescription: Randomizer<String>? = null,
        private var easyRandom: EasyRandom? = null
    ) {

        private val faker = Faker()

        private val fieldPredicateId = FieldPredicates
            .named("id")
            .and(FieldPredicates.ofType(Long::class.java))
            .and(FieldPredicates.inClass(ClientType::class.java))

        private val fieldPredicateName = FieldPredicates
            .named("type")
            .and(FieldPredicates.ofType(String::class.java))
            .and(FieldPredicates.inClass(ClientType::class.java))

        private val fieldPredicateClients = FieldPredicates
            .named("clients")
            .and(FieldPredicates.ofType(MutableList::class.java))
            .and(FieldPredicates.inClass(ClientType::class.java))

        private val defaultRandomizerId = Randomizer { Random.nextLong(1, 20000) }
        private val defaultRandomizerType = Randomizer { faker.book().genre() }
        private val defaultRandomizerClients = Randomizer { mutableListOf<Client>() }

        fun build() = apply {

            val randomParameter = EasyRandomParameters()
                .randomize(fieldPredicateId, randomizerId ?: defaultRandomizerId)
                .randomize(fieldPredicateName, randomizerName ?: defaultRandomizerType)
                .randomize(fieldPredicateClients, randomizerDescription ?: defaultRandomizerClients)
                .objectPoolSize(3)
                .scanClasspathForConcreteTypes(true)

            this.easyRandom = EasyRandom(randomParameter)
        }

        fun nextObject(): ClientType {

            checkNotNull(this.easyRandom) { "please call first the build method!" }
            return this.easyRandom!!.nextObject(ClientType::class.java)

        }

    }
}