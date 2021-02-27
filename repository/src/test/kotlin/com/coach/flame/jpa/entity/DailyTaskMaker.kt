package com.coach.flame.jpa.entity

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.a
import com.natpryce.makeiteasy.MakeItEasy.make
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.util.*

class DailyTaskMaker {

    companion object {

        private val fake = Faker()
        val uuid: Property<DailyTask, UUID> = newProperty()
        val name: Property<DailyTask, String> = newProperty()
        val description: Property<DailyTask, String> = newProperty()
        val date: Property<DailyTask, Date> = newProperty()
        val ticked: Property<DailyTask, Boolean> = newProperty()
        val createdBy: Property<DailyTask, Client> = newProperty()
        val client: Property<DailyTask, Client> = newProperty()

        val DailyTask: Instantiator<DailyTask> = Instantiator {
            DailyTask(
                uuid = it.valueOf(uuid, UUID.randomUUID()),
                name = it.valueOf(name, fake.food().fruit()),
                description = it.valueOf(name, fake.friends().quote()),
                date = it.valueOf(date, fake.date().birthday()),
                ticked = it.valueOf(ticked, false),
                createdBy = it.valueOf(createdBy, make(a(ClientMaker.Client))),
                client = it.valueOf(client, make(a(ClientMaker.Client))),
            )
        }
    }

}