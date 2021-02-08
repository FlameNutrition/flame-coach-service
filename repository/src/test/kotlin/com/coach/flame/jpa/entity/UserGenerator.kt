package com.coach.flame.jpa.entity

import com.github.javafaker.Faker
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.FieldPredicates
import org.jeasy.random.api.Randomizer
import kotlin.random.Random

private val FAKER = Faker()

//FIXME: This id is not recognized
private val randomizerId = Randomizer { Random.nextLong(1, 20000) }
private val randomizerEmail = Randomizer { FAKER.internet().emailAddress() }
private val randomizerPassword = Randomizer { FAKER.internet().password() }

private val randomizerClient = Randomizer { null }

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

private val RANDOM_PARAMETER = EasyRandomParameters()
    .randomize(fieldPredicateEmail, randomizerEmail)
    .randomize(fieldPredicatePassword, randomizerPassword)
    .randomize(fieldPredicateClient, randomizerClient)
    .randomize(fieldPredicateId, randomizerId)
    .scanClasspathForConcreteTypes(true)

private val INSTANCE = EasyRandom(RANDOM_PARAMETER)

fun generateUser(): User {
    return INSTANCE.nextObject(User::class.java)
}