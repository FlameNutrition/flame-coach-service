package com.coach.flame.jpa.entity.maker

import com.coach.flame.domain.maker.ClientDtoBuilder
import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.Client.Companion.toClient
import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.entity.DailyTask
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property
import java.time.LocalDate
import java.util.*

object DailyTaskBuilder {

    private val MAKER: Maker<DailyTask> = an(DailyTaskMaker.DailyTask)

    fun maker(): Maker<DailyTask> {
        return MAKER
    }

    fun default(): DailyTask {
        return maker().make()
    }

}

class DailyTaskMaker {

    companion object {

        private val fake = Faker()
        val uuid: Property<DailyTask, UUID> = Property.newProperty()
        val name: Property<DailyTask, String> = Property.newProperty()
        val description: Property<DailyTask, String> = Property.newProperty()
        val date: Property<DailyTask, LocalDate> = Property.newProperty()
        val ticked: Property<DailyTask, Boolean> = Property.newProperty()
        val createdBy: Property<DailyTask, Coach> = Property.newProperty()
        val client: Property<DailyTask, Client> = Property.newProperty()

        val DailyTask: Instantiator<DailyTask> = Instantiator {
            DailyTask(
                uuid = it.valueOf(uuid, UUID.randomUUID()),
                name = it.valueOf(name, fake.food().fruit()),
                description = it.valueOf(description, fake.friends().quote()),
                date = it.valueOf(date, LocalDate.now()),
                ticked = it.valueOf(ticked, false),
                createdBy = it.valueOf(createdBy, CoachBuilder.default()),
                client = it.valueOf(client, ClientDtoBuilder.makerWithLoginInfo().make().toClient())
            )
        }
    }

}
