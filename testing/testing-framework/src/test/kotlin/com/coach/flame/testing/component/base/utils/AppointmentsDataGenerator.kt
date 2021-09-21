package com.coach.flame.testing.component.base.utils

import com.coach.flame.domain.maker.AppointmentDtoBuilder
import com.coach.flame.domain.maker.AppointmentDtoMaker
import com.coach.flame.domain.maker.IncomeDtoBuilder
import com.coach.flame.jpa.entity.Appointment
import com.coach.flame.jpa.entity.Appointment.Companion.toAppointment
import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.Coach
import com.natpryce.makeiteasy.MakeItEasy.with
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.atomic.AtomicLong

class AppointmentsDataGenerator private constructor() {

    data class Builder(
        private val coach: Coach,
        private val client: Client,
    ) {

        private val identifiers: MutableList<UUID> = mutableListOf()
        private val intervalDates: MutableList<Pair<ZonedDateTime, ZonedDateTime>> = mutableListOf()

        fun addIdentifier(identifier: UUID) = apply { this.identifiers.add(identifier) }

        fun addIdentifierWithInterval(identifier: UUID, from: ZonedDateTime, to: ZonedDateTime) = apply {
            addIdentifier(identifier)
            addInterval(from, to)
        }

        fun addInterval(from: ZonedDateTime, to: ZonedDateTime) = apply { this.intervalDates.add(Pair(from, to)) }

        fun build(): List<Appointment> {

            require(identifiers.size > 0) { "identifiers needs to be greater than zero" }

            if (intervalDates.isNotEmpty()) {
                require(identifiers.size == intervalDates.size) { "identifiers needs to have the same number of values of intervalDates" }
            }

            val listOfAppointments = IntRange(0, identifiers.size - 1)
                .map { index ->
                    val id = AtomicLong(100)
                    var maker = AppointmentDtoBuilder.maker()
                        .but(
                            with(AppointmentDtoMaker.notes, "Appointment"),
                            with(AppointmentDtoMaker.income, IncomeDtoBuilder.accepted()),
                            with(AppointmentDtoMaker.coach, coach.toDto()),
                            with(AppointmentDtoMaker.client, client.toDto()),
                            with(AppointmentDtoMaker.id, id.getAndIncrement()),
                            with(AppointmentDtoMaker.identifier, identifiers[index])
                        )

                    if (intervalDates.isNotEmpty()) {
                        // Update maker with the correct start/end dates
                        maker = maker.but(
                            with(AppointmentDtoMaker.dttmStarts, intervalDates[index].first),
                            with(AppointmentDtoMaker.dttmEnds, intervalDates[index].second)
                        )
                    }

                    maker.make().toAppointment()
                }
                .toList()

            return listOfAppointments
        }
    }

}
