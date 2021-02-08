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
private val randomizerName = Randomizer { FAKER.harryPotter().spell() }
private val randomizerDescription = Randomizer { FAKER.harryPotter().quote() }

private val fieldPredicateId = FieldPredicates
    .named("id")
    .and(FieldPredicates.ofType(Long::class.java))
    .and(FieldPredicates.inClass(DailyTask::class.java))

private val fieldPredicateName = FieldPredicates
    .named("name")
    .and(FieldPredicates.ofType(String::class.java))
    .and(FieldPredicates.inClass(DailyTask::class.java))

private val fieldPredicateDescription = FieldPredicates
    .named("description")
    .and(FieldPredicates.ofType(String::class.java))
    .and(FieldPredicates.inClass(DailyTask::class.java))

private val RANDOM_PARAMETER = EasyRandomParameters()
    .randomize(fieldPredicateName, randomizerName)
    .randomize(fieldPredicateDescription, randomizerDescription)
    .randomize(fieldPredicateId, randomizerId)
    .scanClasspathForConcreteTypes(true)

private val INSTANCE = EasyRandom(RANDOM_PARAMETER)

fun generateDailyTask(): DailyTask {
    return INSTANCE.nextObject(DailyTask::class.java)
}