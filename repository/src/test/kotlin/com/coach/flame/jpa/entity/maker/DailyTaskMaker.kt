package com.coach.flame.jpa.entity.maker

import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.entity.DailyTask
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDate
import java.util.*

class DailyTaskMaker {

    companion object {

        private val fake = Faker()
        val uuid: Property<DailyTask, UUID> = newProperty()
        val name: Property<DailyTask, String> = newProperty()
        val description: Property<DailyTask, String> = newProperty()
        val date: Property<DailyTask, LocalDate> = newProperty()
        val ticked: Property<DailyTask, Boolean> = newProperty()
        val createdBy: Property<DailyTask, Coach> = newProperty()
        val client: Property<DailyTask, Client> = newProperty()

        val DailyTask: Instantiator<DailyTask> = Instantiator {
            DailyTask(
                uuid = it.valueOf(uuid, UUID.randomUUID()),
                name = it.valueOf(name, fake.food().fruit()),
                description = it.valueOf(description, fake.friends().quote()),
                date = it.valueOf(date, LocalDate.now()),
                ticked = it.valueOf(ticked, false),
                createdBy = it.valueOf(createdBy, CoachBuilder.default()),
                client = it.valueOf(client, ClientBuilder.default())
            )
        }
    }

}
