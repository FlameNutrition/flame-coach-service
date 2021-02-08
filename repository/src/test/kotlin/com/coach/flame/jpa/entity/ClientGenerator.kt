package com.coach.flame.jpa.entity

import com.github.javafaker.Faker
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.FieldPredicates
import org.jeasy.random.api.Randomizer
import org.springframework.data.jpa.domain.AbstractPersistable
import kotlin.random.Random

private val FAKER = Faker()

private val randomizerId = Randomizer { Random.nextLong(1, 20000) }
private val randomizerFirstName = Randomizer { FAKER.name().firstName() }
private val randomizerLastName = Randomizer { FAKER.name().lastName() }

private val randomizerUser = Randomizer { generateUser() }
private val randomizerClientType = Randomizer { generateClientType() }
private val randomizerClientMeasureWeight = Randomizer { mutableListOf<ClientMeasureWeight>() }
private val randomizerDailyClientTask = Randomizer { mutableListOf<DailyTask>() }
private val randomizerCreatedBy = Randomizer { generateSysAdmin() }

//FIXME: This id is not recognized
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

private val fieldPredicateCreatedBy = FieldPredicates
    .named("createdBy")
    .and(FieldPredicates.ofType(SysAdmin::class.java))
    .and(FieldPredicates.inClass(Client::class.java))

private val RANDOM_PARAMETER = EasyRandomParameters()
    .randomize(fieldPredicateId, randomizerId)
    .randomize(fieldPredicateFirstName, randomizerFirstName)
    .randomize(fieldPredicateLastName, randomizerLastName)
    .randomize(fieldPredicateUser, randomizerUser)
    .randomize(fieldPredicateClientType, randomizerClientType)
    .randomize(fieldPredicateClientMeasureWeight, randomizerClientMeasureWeight)
    .randomize(fieldPredicateDailyClientTask, randomizerDailyClientTask)
    .randomize(fieldPredicateCreatedBy, randomizerCreatedBy)
    .scanClasspathForConcreteTypes(true)

private val INSTANCE = EasyRandom(RANDOM_PARAMETER)

fun generateClient(): Client {
    return INSTANCE.nextObject(Client::class.java)
}