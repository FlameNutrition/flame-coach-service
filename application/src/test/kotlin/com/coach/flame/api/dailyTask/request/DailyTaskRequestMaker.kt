package com.coach.flame.api.dailyTask.request

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDate
import java.util.*

class DailyTaskRequestMaker {

    companion object {

        private val fake = Faker()
        val name: Property<DailyTaskRequest, String?> = newProperty()
        val description: Property<DailyTaskRequest, String?> = newProperty()
        val date: Property<DailyTaskRequest, String?> = newProperty()
        val toDate: Property<DailyTaskRequest, String?> = newProperty()

        val DailyTaskRequest: Instantiator<DailyTaskRequest> = Instantiator {
            DailyTaskRequest(
                taskName = it.valueOf(name, "Eat  $fake.food().fruit()"),
                taskDescription = it.valueOf(description, fake.friends().quote()),
                date = it.valueOf(date, LocalDate.now().toString()),
                toDate = it.valueOf(toDate, null as String?),
            )
        }
    }

}