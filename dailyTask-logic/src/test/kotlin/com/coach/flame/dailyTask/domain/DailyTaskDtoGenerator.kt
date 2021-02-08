package com.coach.flame.dailyTask.domain

import com.coach.flame.jpa.entity.DailyTask
import com.github.javafaker.Faker
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.FieldPredicates
import org.jeasy.random.api.Randomizer

private val FAKER = Faker()

private val randomizerDescription = Randomizer { FAKER.harryPotter().quote() }

private val randomizerName = Randomizer { FAKER.harryPotter().spell() }

private val fieldPredicateName = FieldPredicates
    .named("name")
    .and(FieldPredicates.ofType(String::class.java))
    .and(FieldPredicates.inClass(DailyTaskDto::class.java))

private val fieldPredicateDescription = FieldPredicates
    .named("description")
    .and(FieldPredicates.ofType(String::class.java))
    .and(FieldPredicates.inClass(DailyTaskDto::class.java))

private val RANDOM_PARAMETER = EasyRandomParameters()
    .randomize(fieldPredicateName, randomizerName)
    .randomize(fieldPredicateDescription, randomizerDescription)

private val EASY_RANDOM_DAILY_TASK_DTO = EasyRandom(RANDOM_PARAMETER)

fun generateDailyTaskDto(): DailyTaskDto {
    return EASY_RANDOM_DAILY_TASK_DTO.nextObject(DailyTaskDto::class.java)
}