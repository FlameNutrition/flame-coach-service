package com.coach.flame.domain

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDate
import java.util.*

class DailyTaskDtoMaker {

    companion object {

        private val fake = Faker()
        val uuid: Property<DailyTaskDto, UUID> = newProperty()
        val name: Property<DailyTaskDto, String> = newProperty()
        val description: Property<DailyTaskDto, String> = newProperty()
        val date: Property<DailyTaskDto, LocalDate> = newProperty()
        val ticked: Property<DailyTaskDto, Boolean> = newProperty()
        val coachIdentifier: Property<DailyTaskDto, UUID?> = newProperty()
        val clientIdentifier: Property<DailyTaskDto, UUID?> = newProperty()

        val DailyTaskDto: Instantiator<DailyTaskDto> = Instantiator {
            DailyTaskDto(
                identifier = it.valueOf(uuid, UUID.randomUUID()),
                name = it.valueOf(name, "Eat ${fake.food().fruit()}"),
                description = it.valueOf(description, fake.friends().quote()),
                date = it.valueOf(date, LocalDate.now()),
                ticked = it.valueOf(ticked, false),
                coachIdentifier = it.valueOf(coachIdentifier, UUID.randomUUID()),
                clientIdentifier = it.valueOf(clientIdentifier, UUID.randomUUID()),
            )
        }
    }

}