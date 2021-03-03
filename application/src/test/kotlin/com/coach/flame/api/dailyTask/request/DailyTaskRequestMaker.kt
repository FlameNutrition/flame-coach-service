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
        val clientIdentifierTask: Property<DailyTaskRequest, String?> = newProperty()
        val clientIdentifierCreator: Property<DailyTaskRequest, String?> = newProperty()

        val DailyTaskRequest: Instantiator<DailyTaskRequest> = Instantiator {
            DailyTaskRequest(
                name = it.valueOf(name, "Eat  $fake.food().fruit()"),
                description = it.valueOf(description, fake.friends().quote()),
                date = it.valueOf(date, LocalDate.now().toString()),
                clientIdentifierTask = it.valueOf(clientIdentifierTask, UUID.randomUUID().toString()),
                clientIdentifierCreator = it.valueOf(clientIdentifierCreator, UUID.randomUUID().toString()),
            )
        }
    }

}