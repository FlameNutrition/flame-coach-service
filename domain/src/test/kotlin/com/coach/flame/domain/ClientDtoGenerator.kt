package com.coach.flame.domain

import com.github.javafaker.Faker
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.FieldPredicates
import org.jeasy.random.api.Randomizer
import java.sql.Date
import java.util.*

class ClientDtoGenerator {

    data class Builder(
        private var randomizerUuid: Randomizer<UUID>? = null,
        private val randomizerFirstName: Randomizer<String>? = null,
        private val randomizerLastName: Randomizer<String>? = null,
        private val randomizerBirthday: Randomizer<Date>? = null,
        private var randomizerCountry: Randomizer<CountryDto>? = null,
        private var randomizerGender: Randomizer<GenderDto>? = null,
        private val randomizerClientType: Randomizer<ClientTypeDto>? = null,
        private var easyRandom: EasyRandom? = null
    ) {

        private val faker = Faker()

        private val countryConfigGenerator = CountryDtoGenerator.Builder().build()
        private val genderConfigGenerator = GenderDtoGenerator.Builder().build()

        private val fieldPredicateUuid = FieldPredicates
            .named("identifier")
            .and(FieldPredicates.ofType(UUID::class.java))
            .and(FieldPredicates.inClass(ClientDto::class.java))

        private val fieldPredicateFirstName = FieldPredicates
            .named("firstName")
            .and(FieldPredicates.ofType(String::class.java))
            .and(FieldPredicates.inClass(ClientDto::class.java))

        private val fieldPredicateLastName = FieldPredicates
            .named("lastName")
            .and(FieldPredicates.ofType(String::class.java))
            .and(FieldPredicates.inClass(ClientDto::class.java))

        private val fieldPredicateBirthday = FieldPredicates
            .named("birthday")
            .and(FieldPredicates.ofType(Date::class.java))
            .and(FieldPredicates.inClass(ClientDto::class.java))

        private val fieldPredicateCountry = FieldPredicates
            .named("country")
            .and(FieldPredicates.ofType(CountryDto::class.java))
            .and(FieldPredicates.inClass(ClientDto::class.java))

        private val fieldPredicateGender = FieldPredicates
            .named("gender")
            .and(FieldPredicates.ofType(GenderDto::class.java))
            .and(FieldPredicates.inClass(ClientDto::class.java))

        private val fieldPredicateClientType = FieldPredicates
            .named("clientType")
            .and(FieldPredicates.ofType(ClientTypeDto::class.java))
            .and(FieldPredicates.inClass(ClientDto::class.java))

        private val defaultRandomizerUuid = Randomizer { UUID.randomUUID() }
        private val defaultRandomizerFirstName = Randomizer { faker.name().firstName() }
        private val defaultRandomizerLastName = Randomizer { faker.name().lastName() }
        private val defaultRandomizerBirthday = Randomizer { faker.name().lastName() }
        private val defaultRandomizerCountry = Randomizer { countryConfigGenerator.nextObject() }
        private val defaultRandomizerGender = Randomizer { genderConfigGenerator.nextObject() }

        private val defaultRandomizerClientType = Randomizer { ClientTypeDto.CLIENT }

        fun withRandomizerUuid(randomizer: Randomizer<UUID>) =
            apply { this.randomizerUuid = randomizer }

        fun withRandomizerCountry(randomizer: Randomizer<CountryDto>) =
            apply { this.randomizerCountry = randomizer }

        fun withRandomizerGender(randomizer: Randomizer<GenderDto>) =
            apply { this.randomizerGender = randomizer }

        fun build() = apply {

            val randomParameter = EasyRandomParameters()
                .randomize(fieldPredicateUuid, randomizerUuid ?: defaultRandomizerUuid)
                .randomize(fieldPredicateFirstName, randomizerFirstName ?: defaultRandomizerFirstName)
                .randomize(fieldPredicateLastName, randomizerLastName ?: defaultRandomizerLastName)
                //FIXME: Check with this happen: Can not set final java.sql.Date field com.coach.flame.jpa.entity.Client.birthday
                //.randomize(fieldPredicateBirthday, randomizerBirthday ?: defaultRandomizerBirthday)
                .randomize(fieldPredicateCountry, randomizerCountry ?: defaultRandomizerCountry)
                .randomize(fieldPredicateGender, randomizerGender ?: defaultRandomizerGender)
                .randomize(fieldPredicateClientType, randomizerClientType ?: defaultRandomizerClientType)
                .objectPoolSize(3)
                .scanClasspathForConcreteTypes(true)

            this.easyRandom = EasyRandom(randomParameter)
        }

        fun nextObject(): ClientDto {

            checkNotNull(this.easyRandom) { "please call first the build method!" }
            return this.easyRandom!!.nextObject(ClientDto::class.java)

        }


    }
}