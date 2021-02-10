package com.coach.flame.jpa.entity

import com.github.javafaker.Faker
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.FieldPredicates
import org.jeasy.random.api.Randomizer
import kotlin.random.Random

class UserGenerator {

    data class Builder(
        private val randomizerId: Randomizer<Long>? = null,
        private val randomizerEmail: Randomizer<String>? = null,
        private val randomizerPassword: Randomizer<String>? = null,
        private val randomizerClient: Randomizer<Client>? = null,
        private var easyRandom: EasyRandom? = null
    ) {

        private val faker = Faker()

        private val fieldPredicateId = FieldPredicates
            .named("id")
            .and(FieldPredicates.ofType(Long::class.java))
            .and(FieldPredicates.inClass(User::class.java))

        private val fieldPredicateEmail = FieldPredicates
            .named("email")
            .and(FieldPredicates.ofType(String::class.java))
            .and(FieldPredicates.inClass(User::class.java))

        private val fieldPredicatePassword = FieldPredicates
            .named("password")
            .and(FieldPredicates.ofType(String::class.java))
            .and(FieldPredicates.inClass(User::class.java))

        private val fieldPredicateClient = FieldPredicates
            .named("client")
            .and(FieldPredicates.ofType(Client::class.java))
            .and(FieldPredicates.inClass(User::class.java))

        private val defaultRandomizerId = Randomizer { Random.nextLong(1, 20000) }
        private val defaultRandomizerEmail = Randomizer { faker.internet().emailAddress() }
        private val defaultRandomizerPassword = Randomizer { faker.internet().password() }
        private val defaultRandomizerClient = Randomizer { null }

        fun build() = apply {

            val randomParameter = EasyRandomParameters()
                .randomize(fieldPredicateId, randomizerId ?: defaultRandomizerId)
                .randomize(fieldPredicateEmail, randomizerEmail ?: defaultRandomizerEmail)
                .randomize(fieldPredicatePassword, randomizerPassword ?: defaultRandomizerPassword)
                .randomize(fieldPredicateClient, randomizerClient ?: defaultRandomizerClient)
                .objectPoolSize(3)
                .scanClasspathForConcreteTypes(true)

            this.easyRandom = EasyRandom(randomParameter)
        }

        fun nextObject(): User {

            checkNotNull(this.easyRandom) { "please call first the build method!" }
            return this.easyRandom!!.nextObject(User::class.java)

        }

    }
}