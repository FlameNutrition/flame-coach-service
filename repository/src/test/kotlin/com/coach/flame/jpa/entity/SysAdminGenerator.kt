package com.coach.flame.jpa.entity

import com.github.javafaker.Faker
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.FieldPredicates
import org.jeasy.random.api.Randomizer
import kotlin.random.Random

class SysAdminGenerator {

    data class Builder(
        private val randomizerId: Randomizer<Long>? = null,
        private val randomizerRef: Randomizer<String>? = null,
        private val randomizerDescription: Randomizer<String>? = null,
        private var easyRandom: EasyRandom? = null
    ) {

        private val faker = Faker()

        private val fieldPredicateId = FieldPredicates
            .named("id")
            .and(FieldPredicates.ofType(Long::class.java))
            .and(FieldPredicates.inClass(SysAdmin::class.java))

        private val fieldPredicateRef = FieldPredicates
            .named("reference")
            .and(FieldPredicates.ofType(String::class.java))
            .and(FieldPredicates.inClass(SysAdmin::class.java))

        private val fieldPredicateClients = FieldPredicates
            .named("description")
            .and(FieldPredicates.ofType(String::class.java))
            .and(FieldPredicates.inClass(SysAdmin::class.java))

        private val defaultRandomizerId = Randomizer { Random.nextLong(1, 20000) }
        private val defaultRandomizerName = Randomizer { faker.book().genre() }
        private val defaultRandomizerDescription = Randomizer { faker.yoda().quote() }

        fun build() = apply {

            val randomParameter = EasyRandomParameters()
                .randomize(fieldPredicateId, randomizerId ?: defaultRandomizerId)
                .randomize(fieldPredicateRef, randomizerRef ?: defaultRandomizerName)
                .randomize(fieldPredicateClients, randomizerDescription ?: defaultRandomizerDescription)
                .objectPoolSize(3)
                .scanClasspathForConcreteTypes(true)

            this.easyRandom = EasyRandom(randomParameter)
        }

        fun nextObject(): SysAdmin {

            checkNotNull(this.easyRandom) { "please call first the build method!" }
            return this.easyRandom!!.nextObject(SysAdmin::class.java)

        }


    }
}