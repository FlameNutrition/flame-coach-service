package com.coach.flame.testing.component.base.utils

import com.coach.flame.domain.maker.AppointmentDtoBuilder
import com.coach.flame.domain.maker.AppointmentDtoMaker
import com.coach.flame.jpa.entity.Appointment
import com.coach.flame.jpa.entity.Appointment.Companion.toAppointment
import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.entity.maker.CoachBuilder
import com.coach.flame.jpa.entity.maker.CoachMaker
import com.natpryce.makeiteasy.MakeItEasy
import com.natpryce.makeiteasy.MakeItEasy.*
import io.mockk.Answer
import io.mockk.Call
import java.time.LocalDateTime
import java.util.*

object AppointmentsHelper {

    val oneAppointment: (Coach, Client, UUID) -> (Appointment) = { coach: Coach, client: Client, identifier: UUID ->
        AppointmentDtoBuilder.maker()
            .but(with(AppointmentDtoMaker.id, 100L),
                with(AppointmentDtoMaker.identifier, identifier),
                with(AppointmentDtoMaker.notes, "First appointment"),
                with(AppointmentDtoMaker.dttm, LocalDateTime.parse("2021-07-14T10:52:52")),
                with(AppointmentDtoMaker.price, 100.5f),
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
                        with(AppointmentDtoMaker.dttm, LocalDateTime.parse("2021-07-14T10:52:52")),
                        with(AppointmentDtoMaker.price, 100.5f),
                        with(AppointmentDtoMaker.coach, coach.toDto()),
                        with(AppointmentDtoMaker.client, client.toDto()))
                    .make()
                    .toAppointment(),
                AppointmentDtoBuilder.maker()
                    .but(with(AppointmentDtoMaker.id, 200L),
                        with(AppointmentDtoMaker.identifier, identifiers.last()),
                        with(AppointmentDtoMaker.notes, "Review appointment"),
                        with(AppointmentDtoMaker.dttm, LocalDateTime.parse("2021-07-20T10:52:52")),
                        with(AppointmentDtoMaker.price, 200.5f),
                        with(AppointmentDtoMaker.coach, coach.toDto()),
                        with(AppointmentDtoMaker.client, client.toDto()))
                    .make()
                    .toAppointment()
            )
        }

    val twoAppointmentsDifferentClient: (Coach, Client, Client, List<UUID>) -> (List<Appointment>) =
        { coach: Coach, client1: Client, client2: Client, identifiers: List<UUID> ->
            listOf(
                AppointmentDtoBuilder.maker()
                    .but(with(AppointmentDtoMaker.id, 100L),
                        with(AppointmentDtoMaker.identifier, identifiers.first()),
                        with(AppointmentDtoMaker.notes, "First appointment"),
                        with(AppointmentDtoMaker.dttm, LocalDateTime.parse("2021-07-14T10:52:52")),
                        with(AppointmentDtoMaker.price, 100.5f),
                        with(AppointmentDtoMaker.coach, coach.toDto()),
                        with(AppointmentDtoMaker.client, client1.toDto()))
                    .make()
                    .toAppointment(),
                AppointmentDtoBuilder.maker()
                    .but(with(AppointmentDtoMaker.id, 200L),
                        with(AppointmentDtoMaker.identifier, identifiers.last()),
                        with(AppointmentDtoMaker.notes, "Review appointment"),
                        with(AppointmentDtoMaker.dttm, LocalDateTime.parse("2021-07-20T10:52:52")),
                        with(AppointmentDtoMaker.price, 200.5f),
                        with(AppointmentDtoMaker.coach, coach.toDto()),
                        with(AppointmentDtoMaker.client, client2.toDto()))
                    .make()
                    .toAppointment()
            )
        }
}
