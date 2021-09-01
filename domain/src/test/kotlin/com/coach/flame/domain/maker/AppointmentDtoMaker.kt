package com.coach.flame.domain.maker

import com.coach.flame.domain.*
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.*

class AppointmentDtoMaker {

    companion object {

        val id: Property<AppointmentDto, Long?> = newProperty()
        val identifier: Property<AppointmentDto, UUID> = newProperty()
        val dttmStarts: Property<AppointmentDto, ZonedDateTime> = newProperty()
        val dttmEnds: Property<AppointmentDto, ZonedDateTime> = newProperty()
        val delete: Property<AppointmentDto, Boolean> = newProperty()
        val coach: Property<AppointmentDto, CoachDto?> = newProperty()
        val client: Property<AppointmentDto, ClientDto?> = newProperty()
        val income: Property<AppointmentDto, IncomeDto> = newProperty()
        val currency: Property<AppointmentDto, Currency> = newProperty()
        val notes: Property<AppointmentDto, String?> = newProperty()

        val AppointmentDto: Instantiator<AppointmentDto> = Instantiator {
            AppointmentDto(
                identifier = it.valueOf(identifier, UUID.randomUUID()),
                id = it.valueOf(id, null as Long?),
                dttmStarts = it.valueOf(dttmStarts, ZonedDateTime.now()),
                dttmEnds = it.valueOf(dttmEnds, ZonedDateTime.now().plusDays(1)),
                delete = it.valueOf(delete, false),
                coach = it.valueOf(coach, null as CoachDto?),
                client = it.valueOf(client, null as ClientDto?),
                income = it.valueOf(income, IncomeDto()),
                currency = it.valueOf(currency, Currency.getInstance("GBP")),
                notes = it.valueOf(notes, null as String?),
            )
        }
    }

}
