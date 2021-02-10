package com.coach.flame.jpa.entity

import com.github.javafaker.Faker
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.FieldPredicates
import org.jeasy.random.api.Randomizer
import java.sql.Date
import kotlin.random.Random

class ClientGenerator {

    data class Builder(
        private val randomizerId: Randomizer<Long>? = null,
        private val randomizerFirstName: Randomizer<String>? = null,
        private val randomizerLastName: Randomizer<String>? = null,
        private val randomizerBirthday: Randomizer<Date>? = null,
        private val randomizerUser: Randomizer<User>? = null,
        private val randomizerClientType: Randomizer<ClientType>? = null,
        private val randomizerClientMeasureWeight: Randomizer<MutableList<ClientMeasureWeight>>? = null,
        private val randomizerDailyClientTask: Randomizer<MutableList<DailyTask>>? = null,
        private var easyRandom: EasyRandom? = null
    ) {

        private val faker = Faker()

        private val fieldPredicateId = FieldPredicates
            .named("id")
            .and(FieldPredicates.ofType(Long::class.java))
            .and(FieldPredicates.inClass(Client::class.java))

        private val fieldPredicateFirstName = FieldPredicates
            .named("firstName")
            .and(FieldPredicates.ofType(String::class.java))
            .and(FieldPredicates.inClass(Client::class.java))

        private val fieldPredicateLastName = FieldPredicates
            .named("lastName")
            .and(FieldPredicates.ofType(String::class.java))
            .and(FieldPredicates.inClass(Client::class.java))

        private val fieldPredicateBirthday = FieldPredicates
            .named("birthday")
            .and(FieldPredicates.ofType(Date::class.java))
            .and(FieldPredicates.inClass(Client::class.java))

        private val fieldPredicateUser = FieldPredicates
            .named("user")
            .and(FieldPredicates.ofType(User::class.java))
            .and(FieldPredicates.inClass(Client::class.java))

        private val fieldPredicateClientType = FieldPredicates
            .named("clientType")
            .and(FieldPredicates.ofType(ClientType::class.java))
            .and(FieldPredicates.inClass(Client::class.java))

        private val fieldPredicateClientMeasureWeight = FieldPredicates
            .named("clientMeasureWeight")
            .and(FieldPredicates.ofType(MutableList::class.java))
            .and(FieldPredicates.inClass(Client::class.java))

        private val fieldPredicateDailyClientTask = FieldPredicates
            .named("dailyClientTask")
            .and(FieldPredicates.ofType(MutableList::class.java))
            .and(FieldPredicates.inClass(Client::class.java))

        private val defaultRandomizerId = Randomizer { Random.nextLong(1, 20000) }
        private val defaultRandomizerFirstName = Randomizer { faker.name().firstName() }
        private val defaultRandomizerLastName = Randomizer { faker.name().lastName() }
        private val defaultRandomizerBirthday = Randomizer { faker.name().lastName() }

        private val defaultRandomizerUser = Randomizer { UserGenerator.Builder().build().nextObject() }
        private val defaultRandomizerClientType = Randomizer { ClientTypeGenerator.Builder().build().nextObject() }
        private val defaultRandomizerClientMeasureWeight = Randomizer { mutableListOf<ClientMeasureWeight>() }
        private val defaultRandomizerDailyClientTask = Randomizer { mutableListOf<DailyTask>() }

        fun build() = apply {

            val randomParameter = EasyRandomParameters()
                .randomize(fieldPredicateId, randomizerId ?: defaultRandomizerId)
                .randomize(fieldPredicateFirstName, randomizerFirstName ?: defaultRandomizerFirstName)
                .randomize(fieldPredicateLastName, randomizerLastName ?: defaultRandomizerLastName)
                //FIXME: Check with this happen: Can not set final java.sql.Date field com.coach.flame.jpa.entity.Client.birthday
                //.randomize(fieldPredicateBirthday, randomizerBirthday ?: defaultRandomizerBirthday)
                .randomize(fieldPredicateUser, randomizerUser ?: defaultRandomizerUser)
                .randomize(fieldPredicateClientType, randomizerClientType ?: defaultRandomizerClientType)
                .randomize(
                    fieldPredicateClientMeasureWeight,
                    randomizerClientMeasureWeight ?: defaultRandomizerClientMeasureWeight
                )
                .randomize(fieldPredicateDailyClientTask, randomizerDailyClientTask ?: defaultRandomizerDailyClientTask)
                .objectPoolSize(3)
                .scanClasspathForConcreteTypes(true)

            this.easyRandom = EasyRandom(randomParameter)
        }

        fun nextObject(): Client {

            checkNotNull(this.easyRandom) { "please call first the build method!" }
            return this.easyRandom!!.nextObject(Client::class.java)

        }


    }
}