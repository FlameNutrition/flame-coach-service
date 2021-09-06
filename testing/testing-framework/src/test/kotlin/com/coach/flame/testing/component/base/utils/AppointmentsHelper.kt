package com.coach.flame.testing.component.base.utils

import com.coach.flame.domain.IncomeDto
import com.coach.flame.domain.maker.AppointmentDtoBuilder
import com.coach.flame.domain.maker.AppointmentDtoMaker
import com.coach.flame.domain.maker.IncomeDtoBuilder
import com.coach.flame.jpa.entity.Appointment
import com.coach.flame.jpa.entity.Appointment.Companion.toAppointment
import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.Coach
import com.natpryce.makeiteasy.MakeItEasy.*
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.atomic.AtomicLong

object AppointmentsHelper {

    val oneAppointment: (Coach, Client, UUID) -> (Appointment) = { coach: Coach, client: Client, identifier: UUID ->
        AppointmentDtoBuilder.maker()
            .but(with(AppointmentDtoMaker.id, 100L),
                with(AppointmentDtoMaker.identifier, identifier),
                with(AppointmentDtoMaker.notes, "First appointment"),
                with(AppointmentDtoMaker.dttmStarts, ZonedDateTime.parse("2021-07-14T10:52:52+01:00")),
                with(AppointmentDtoMaker.dttmEnds, ZonedDateTime.parse("2021-07-14T11:52:52+01:00")),
                with(AppointmentDtoMaker.income, IncomeDto(100.5f)),
                with(AppointmentDtoMaker.coach, coach.toDto()),
                with(AppointmentDtoMaker.client, client.toDto()))
            .make()
            .toAppointment()
    }

    val twoAppointments: (Coach, Client, List<UUID>) -> (List<Appointment>) =
        { coach: Coach, client: Client, identifiers: List<UUID> ->
            listOf(
                AppointmentDtoBuilder.maker()
                    .but(with(AppointmentDtoMaker.id, 100L),
                        with(AppointmentDtoMaker.identifier, identifiers.first()),
                        with(AppointmentDtoMaker.notes, "First appointment"),
                        with(AppointmentDtoMaker.dttmStarts, ZonedDateTime.parse("2021-07-14T10:52:52+01:00")),
                        with(AppointmentDtoMaker.dttmEnds, ZonedDateTime.parse("2021-07-14T11:52:52+01:00")),
                        with(AppointmentDtoMaker.income, IncomeDto(100.5f)),
                        with(AppointmentDtoMaker.coach, coach.toDto()),
                        with(AppointmentDtoMaker.client, client.toDto()))
                    .make()
                    .toAppointment(),
                AppointmentDtoBuilder.maker()
                    .but(with(AppointmentDtoMaker.id, 200L),
                        with(AppointmentDtoMaker.identifier, identifiers.last()),
                        with(AppointmentDtoMaker.notes, "Review appointment"),
                        with(AppointmentDtoMaker.dttmStarts, ZonedDateTime.parse("2021-07-20T10:52:52+01:00")),
                        with(AppointmentDtoMaker.dttmEnds, ZonedDateTime.parse("2021-07-20T11:52:52+01:00")),
                        with(AppointmentDtoMaker.income, IncomeDto(200.5f)),
                        with(AppointmentDtoMaker.coach, coach.toDto()),
                        with(AppointmentDtoMaker.client, client.toDto()))
                    .make()
                    .toAppointment()
            )
        }

    val multipleAppointments: (Coach, Client, List<UUID>, List<String>, List<String>) -> (List<Appointment>) =
        { coach: Coach, client: Client, identifiers: List<UUID>, from: List<String>, to: List<String> ->
            val id = AtomicLong(100)
            val maker = AppointmentDtoBuilder.maker()
                .but(with(AppointmentDtoMaker.notes, "Appointment"),
                    with(AppointmentDtoMaker.income, IncomeDtoBuilder.accepted()),
                    with(AppointmentDtoMaker.coach, coach.toDto()),
                    with(AppointmentDtoMaker.client, client.toDto()))

            val listOfAppointments = mutableListOf<Appointment>()

            IntRange(0, identifiers.size - 1).forEach { index ->
                listOfAppointments.add(maker
                    .but(with(AppointmentDtoMaker.id, id.getAndIncrement()),
                        with(AppointmentDtoMaker.identifier, identifiers[index]),
                        with(AppointmentDtoMaker.dttmStarts, ZonedDateTime.parse(from[index])),
                        with(AppointmentDtoMaker.dttmEnds, ZonedDateTime.parse(to[index])))
                    .make().toAppointment())
            }

            listOfAppointments
        }

    val twoAppointmentsDifferentClient: (Coach, Client, Client, List<UUID>) -> (List<Appointment>) =
        { coach: Coach, client1: Client, client2: Client, identifiers: List<UUID> ->
            listOf(
                AppointmentDtoBuilder.maker()
                    .but(with(AppointmentDtoMaker.id, 100L),
                        with(AppointmentDtoMaker.identifier, identifiers.first()),
                        with(AppointmentDtoMaker.notes, "First appointment"),
                        with(AppointmentDtoMaker.dttmStarts, ZonedDateTime.parse("2021-07-14T10:52:52+01:00")),
                        with(AppointmentDtoMaker.dttmEnds, ZonedDateTime.parse("2021-07-14T11:52:52+01:00")),
                        with(AppointmentDtoMaker.income, IncomeDto(100.5f)),
                        with(AppointmentDtoMaker.coach, coach.toDto()),
                        with(AppointmentDtoMaker.client, client1.toDto()))
                    .make()
                    .toAppointment(),
                AppointmentDtoBuilder.maker()
                    .but(with(AppointmentDtoMaker.id, 200L),
                        with(AppointmentDtoMaker.identifier, identifiers.last()),
                        with(AppointmentDtoMaker.notes, "Review appointment"),
                        with(AppointmentDtoMaker.dttmStarts, ZonedDateTime.parse("2021-07-20T10:52:52+01:00")),
                        with(AppointmentDtoMaker.dttmEnds, ZonedDateTime.parse("2021-07-20T11:52:52+01:00")),
                        with(AppointmentDtoMaker.income, IncomeDto(200.5f)),
                        with(AppointmentDtoMaker.coach, coach.toDto()),
                        with(AppointmentDtoMaker.client, client2.toDto()))
                    .make()
                    .toAppointment()
            )
        }

}
