package com.coach.flame.jpa.entity

import com.github.javafaker.Faker
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.FieldPredicates
import org.jeasy.random.api.Randomizer
import kotlin.random.Random

private val FAKER = Faker()

private val randomizerId = Randomizer { Random.nextLong(1, 20000) }
private val randomizerType = Randomizer { FAKER.book().genre() }

private val randomizerClients = Randomizer { mutableListOf<Client>() }

//FIXME: This id is not recognized
private val fieldPredicateId = FieldPredicates
    .named("id")
    .and(FieldPredicates.ofType(Long::class.java))
    .and(FieldPredicates.inClass(ClientType::class.java))

private val fieldPredicateType = FieldPredicates
    .named("type")
    .and(FieldPredicates.ofType(String::class.java))
    .and(FieldPredicates.inClass(ClientType::class.java))

private val fieldPredicateClients = FieldPredicates
    .named("clients")
    .and(FieldPredicates.ofType(MutableList::class.java))
    .and(FieldPredicates.inClass(ClientType::class.java))

private val RANDOM_PARAMETER = EasyRandomParameters()
    .randomize(fieldPredicateType, randomizerType)
    .randomize(fieldPredicateClients, randomizerClients)
    .randomize(fieldPredicateId, randomizerId)
    .scanClasspathForConcreteTypes(true)

private val INSTANCE = EasyRandom(RANDOM_PARAMETER)

fun generateClientType(): ClientType {
    return INSTANCE.nextObject(ClientType::class.java)
}