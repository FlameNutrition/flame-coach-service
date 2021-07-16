package com.coach.flame.domain.maker

import com.coach.flame.domain.*
import com.github.javafaker.Bool
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class AppointmentDtoMaker {

    companion object {

        val id: Property<AppointmentDto, Long?> = newProperty()
        val identifier: Property<AppointmentDto, UUID> = newProperty()
        val dttm: Property<AppointmentDto, LocalDateTime> = newProperty()
        val dttmTxt: Property<AppointmentDto, String?> = newProperty()
        val delete: Property<AppointmentDto, Boolean> = newProperty()
        val coach: Property<AppointmentDto, CoachDto?> = newProperty()
        val client: Property<AppointmentDto, ClientDto?> = newProperty()
        val price: Property<AppointmentDto, Float> = newProperty()
        val currency: Property<AppointmentDto, Currency> = newProperty()
        val notes: Property<AppointmentDto, String?> = newProperty()

        val AppointmentDto: Instantiator<AppointmentDto> = Instantiator {
            AppointmentDto(
                identifier = it.valueOf(identifier, UUID.randomUUID()),
                id = it.valueOf(id, null as Long?),
                dttm = it.valueOf(dttm, LocalDateTime.now()),
                dttmTxt = it.valueOf(dttmTxt, null as String?),
                delete = it.valueOf(delete, false),
                coach = it.valueOf(coach, null as CoachDto?),
                client = it.valueOf(client, null as ClientDto?),
                price = it.valueOf(price, 0.0f),
                currency = it.valueOf(currency, Currency.getInstance("GBP")),
                notes = it.valueOf(notes, null as String?),
            )
        }
    }

}
