package com.coach.flame.domain.maker

import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.CoachDto
import com.coach.flame.domain.DailyTaskDto
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property
import java.time.LocalDate
import java.util.*

object DailyTaskDtoBuilder {

    private val MAKER: Maker<DailyTaskDto> = an(DailyTaskDtoMaker.DailyTaskDto)

    fun maker(): Maker<DailyTaskDto> {
        return MAKER
    }

    fun default(): DailyTaskDto {
        return maker().make()
    }

}

class DailyTaskDtoMaker {

    companion object {

        private val fake = Faker()
        val id: Property<DailyTaskDto, Long?> = Property.newProperty()
        val uuid: Property<DailyTaskDto, UUID> = Property.newProperty()
        val name: Property<DailyTaskDto, String> = Property.newProperty()
        val description: Property<DailyTaskDto, String> = Property.newProperty()
        val date: Property<DailyTaskDto, LocalDate> = Property.newProperty()
        val ticked: Property<DailyTaskDto, Boolean> = Property.newProperty()
        val coachIdentifier: Property<DailyTaskDto, UUID?> = Property.newProperty()
        val clientIdentifier: Property<DailyTaskDto, UUID?> = Property.newProperty()
        val client: Property<DailyTaskDto, ClientDto> = Property.newProperty()
        val coach: Property<DailyTaskDto, CoachDto> = Property.newProperty()

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
