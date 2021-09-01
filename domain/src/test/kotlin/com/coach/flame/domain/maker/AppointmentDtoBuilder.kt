package com.coach.flame.domain.maker

import com.coach.flame.domain.AppointmentDto
import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.CoachDto
import com.coach.flame.domain.IncomeDto
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property
import java.time.ZonedDateTime
import java.util.*

object AppointmentDtoBuilder {

    private val MAKER: Maker<AppointmentDto> = an(AppointmentDtoMaker.AppointmentDto)

    fun maker(): Maker<AppointmentDto> {
        return MAKER
    }

    fun default(): AppointmentDto {
        return maker().make()
    }

}

class AppointmentDtoMaker {

    companion object {

        val id: Property<AppointmentDto, Long?> = Property.newProperty()
        val identifier: Property<AppointmentDto, UUID> = Property.newProperty()
        val dttmStarts: Property<AppointmentDto, ZonedDateTime> = Property.newProperty()
        val dttmEnds: Property<AppointmentDto, ZonedDateTime> = Property.newProperty()
        val delete: Property<AppointmentDto, Boolean> = Property.newProperty()
        val coach: Property<AppointmentDto, CoachDto?> = Property.newProperty()
        val client: Property<AppointmentDto, ClientDto?> = Property.newProperty()
        val income: Property<AppointmentDto, IncomeDto> = Property.newProperty()
        val currency: Property<AppointmentDto, Currency> = Property.newProperty()
        val notes: Property<AppointmentDto, String?> = Property.newProperty()

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
