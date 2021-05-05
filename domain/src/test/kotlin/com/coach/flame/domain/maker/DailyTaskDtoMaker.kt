package com.coach.flame.domain.maker

import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.CoachDto
import com.coach.flame.domain.DailyTaskDto
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDate
import java.util.*

class DailyTaskDtoMaker {

    companion object {

        private val fake = Faker()
        val id: Property<DailyTaskDto, Long?> = newProperty()
        val uuid: Property<DailyTaskDto, UUID> = newProperty()
        val name: Property<DailyTaskDto, String> = newProperty()
        val description: Property<DailyTaskDto, String> = newProperty()
        val date: Property<DailyTaskDto, LocalDate> = newProperty()
        val ticked: Property<DailyTaskDto, Boolean> = newProperty()
        val coachIdentifier: Property<DailyTaskDto, UUID?> = newProperty()
        val clientIdentifier: Property<DailyTaskDto, UUID?> = newProperty()
        val client: Property<DailyTaskDto, ClientDto> = newProperty()
        val coach: Property<DailyTaskDto, CoachDto> = newProperty()

        val DailyTaskDto: Instantiator<DailyTaskDto> = Instantiator {
            DailyTaskDto(
                id = it.valueOf(id, null as Long?),
                identifier = it.valueOf(uuid, UUID.randomUUID()),
                name = it.valueOf(name, "Eat ${fake.food().fruit()}"),
                description = it.valueOf(description, fake.friends().quote()),
                date = it.valueOf(date, LocalDate.now()),
                ticked = it.valueOf(ticked, false),
                coachIdentifier = it.valueOf(coachIdentifier, UUID.randomUUID()),
                clientIdentifier = it.valueOf(clientIdentifier, UUID.randomUUID()),
                client = it.valueOf(client, ClientDtoBuilder.default()),
                coach = it.valueOf(coach, CoachDtoBuilder.default()),
            )
        }
    }

}
