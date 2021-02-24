package com.coach.flame.jpa.entity

import com.github.javafaker.Faker
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.FieldPredicates
import org.jeasy.random.api.Randomizer
import java.sql.Date
import java.util.*
import kotlin.random.Random

class ClientGenerator {

    data class Builder(
        private val randomizerId: Randomizer<Long>? = null,
        private var randomizerUuid: Randomizer<UUID>? = null,
        private val randomizerFirstName: Randomizer<String>? = null,
        private val randomizerLastName: Randomizer<String>? = null,
        private val randomizerBirthday: Randomizer<Date>? = null,
        private var randomizerCountry: Randomizer<CountryConfig>? = null,
        private var randomizerGender: Randomizer<GenderConfig>? = null,
        private val randomizerUser: Randomizer<User>? = null,
        private var randomizerClientType: Randomizer<ClientType>? = null,
        private val randomizerClientMeasureWeight: Randomizer<MutableList<ClientMeasureWeight>>? = null,
        private val randomizerDailyClientTask: Randomizer<MutableList<DailyTask>>? = null,
        private var easyRandom: EasyRandom? = null
    ) {

        private val faker = Faker()

        private val countryConfigGenerator = CountryConfigGenerator.Builder().build()
        private val genderConfigGenerator = GenderConfigGenerator.Builder().build()
        private val userGenerator = UserGenerator.Builder().build()
        private val clientTypeGenerator = ClientTypeGenerator.Builder().build()

        private val fieldPredicateId = FieldPredicates
            .named("id")
            .and(FieldPredicates.ofType(Long::class.java))
            .and(FieldPredicates.inClass(Client::class.java))

        private val fieldPredicateUuid = FieldPredicates
            .named("uuid")
            .and(FieldPredicates.ofType(UUID::class.java))
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

        private val fieldPredicateCountry = FieldPredicates
            .named("country")
            .and(FieldPredicates.ofType(CountryConfig::class.java))
            .and(FieldPredicates.inClass(Client::class.java))

        private val fieldPredicateGender = FieldPredicates
            .named("gender")
            .and(FieldPredicates.ofType(GenderConfig::class.java))
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
        private val defaultRandomizerUuid = Randomizer { UUID.randomUUID() }
        private val defaultRandomizerFirstName = Randomizer { faker.name().firstName() }
        private val defaultRandomizerLastName = Randomizer { faker.name().lastName() }
        private val defaultRandomizerBirthday = Randomizer { faker.name().lastName() }
        private val defaultRandomizerCountry = Randomizer { countryConfigGenerator.nextObject() }
        private val defaultRandomizerGender = Randomizer { genderConfigGenerator.nextObject() }

        private val defaultRandomizerUser = Randomizer { userGenerator.nextObject() }
        private val defaultRandomizerClientType = Randomizer { clientTypeGenerator.nextObject() }
        private val defaultRandomizerClientMeasureWeight = Randomizer { mutableListOf<ClientMeasureWeight>() }
        private val defaultRandomizerDailyClientTask = Randomizer { mutableListOf<DailyTask>() }

        fun withRandomizerUuid(randomizer: Randomizer<UUID>) =
            apply { this.randomizerUuid = randomizer }

        fun withRandomizerCountry(randomizer: Randomizer<CountryConfig>) =
            apply { this.randomizerCountry = randomizer }

        fun withRandomizerGender(randomizer: Randomizer<GenderConfig>) =
            apply { this.randomizerGender = randomizer }

        fun withRandomizerClientType(randomizer: Randomizer<ClientType>) =
            apply { this.randomizerClientType = randomizer }

        fun build() = apply {

            val randomParameter = EasyRandomParameters()
                .randomize(fieldPredicateId, randomizerId ?: defaultRandomizerId)
                .randomize(fieldPredicateUuid, randomizerUuid ?: defaultRandomizerUuid)
                .randomize(fieldPredicateFirstName, randomizerFirstName ?: defaultRandomizerFirstName)
                .randomize(fieldPredicateLastName, randomizerLastName ?: defaultRandomizerLastName)
                //FIXME: Check with this happen: Can not set final java.sql.Date field com.coach.flame.jpa.entity.Client.birthday
                //.randomize(fieldPredicateBirthday, randomizerBirthday ?: defaultRandomizerBirthday)
                .randomize(fieldPredicateCountry, randomizerCountry ?: defaultRandomizerCountry)
                .randomize(fieldPredicateGender, randomizerGender ?: defaultRandomizerGender)
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