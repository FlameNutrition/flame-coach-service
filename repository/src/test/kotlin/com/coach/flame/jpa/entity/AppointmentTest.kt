package com.coach.flame.jpa.entity

import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.CoachDto
import com.coach.flame.domain.maker.*
import com.coach.flame.jpa.entity.Appointment.Companion.toAppointment
import com.coach.flame.jpa.entity.maker.ClientBuilder
import com.coach.flame.jpa.entity.maker.CoachBuilder
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class AppointmentTest {

    @Test
    fun `test convert appointment to dto all values`() {

        val dateNow = LocalDateTime.parse("2021-07-15T04:52:52.389929")
        val dttmEnds = LocalDateTime.parse("2021-07-15T04:52:52.389929")
        val identifier = UUID.randomUUID()

        val appointment = Appointment(
            uuid = identifier,
            coach = CoachBuilder.default(),
            client = ClientBuilder.default(),
            income = Income(),
            dttmStarts = dateNow,
            dttmEnds = dttmEnds,
            delete = true,
            currency = "GBP",
            notes = "This is a note"
        )

        val dto = appointment.toDto()

        then(dto.identifier).isEqualTo(identifier)
        then(dto.coach).isNotNull
        then(dto.client).isNotNull
        then(dto.income).isNotNull
        then(dto.dttmStarts).isEqualTo(ZonedDateTime.parse("2021-07-15T04:52:52.389929+01:00"))
        then(dto.dttmEnds).isEqualTo(ZonedDateTime.parse("2021-07-15T04:52:52.389929+01:00"))
        then(dto.delete).isTrue
        then(dto.notes).isEqualTo("This is a note")

    }

    @Test
    fun `test convert appointment to dto all values but different zoneId`() {

        val dttmStarts = LocalDateTime.parse("2021-07-15T09:52:52.389929")
        val dttmEnds = LocalDateTime.parse("2021-07-15T09:52:52.389929")
        val identifier = UUID.randomUUID()

        val appointment = Appointment(
            uuid = identifier,
            coach = CoachBuilder.default(),
            client = ClientBuilder.default(),
            income = Income(),
            dttmStarts = dttmStarts,
            dttmEnds = dttmEnds,
            delete = true,
            currency = "GBP",
            notes = "This is a note"
        )

        val dto = appointment.toDto(ZoneId.of("US/Eastern"))

        then(dto.dttmStarts).isEqualTo(ZonedDateTime.parse("2021-07-15T04:52:52.389929-04:00"))
        then(dto.dttmEnds).isEqualTo(ZonedDateTime.parse("2021-07-15T04:52:52.389929-04:00"))

    }

    @Test
    fun `test convert dto to entity all values`() {

        val startDate = ZonedDateTime.parse("2021-07-15T04:52:52.389929-04:00")
        val endDate = ZonedDateTime.parse("2021-07-15T04:52:52.389929-04:00")

        val appointment = AppointmentDtoBuilder.maker()
            .but(with(AppointmentDtoMaker.income, IncomeDtoBuilder.maker()),
                with(AppointmentDtoMaker.delete, true),
                with(AppointmentDtoMaker.coach, CoachDtoBuilder.makerWithLoginInfo().make()),
                with(AppointmentDtoMaker.client, ClientDtoBuilder.makerWithLoginInfo().make()),
                with(AppointmentDtoMaker.dttmStarts, startDate),
                with(AppointmentDtoMaker.dttmEnds, endDate),
                with(AppointmentDtoMaker.notes, "This is a note"))
            .make()

        val entity = appointment.toAppointment()

        then(entity.coach).isNotNull
        then(entity.client).isNotNull
        then(entity.income).isNotNull
        then(entity.dttmStarts).isEqualTo(LocalDateTime.parse("2021-07-15T09:52:52.389929"))
        then(entity.dttmEnds).isEqualTo(LocalDateTime.parse("2021-07-15T09:52:52.389929"))
        then(entity.currency).isEqualTo("GBP")
        then(entity.notes).isEqualTo("This is a note")
        then(entity.delete).isTrue

    }

    @Test
    fun `test convert dto to entity missing required attr`() {

        val exception2 = catchThrowable {
            AppointmentDtoBuilder.maker()
                .but(with(AppointmentDtoMaker.coach, null as CoachDto?),
                    with(AppointmentDtoMaker.client, ClientDtoBuilder.makerWithLoginInfo().make()))
                .make()
                .toAppointment()
        }

        then(exception2).isInstanceOf(IllegalStateException::class.java)
        then(exception2).hasMessageContaining("coach can not be null")

        val exception3 = catchThrowable {
            AppointmentDtoBuilder.maker()
                .but(with(AppointmentDtoMaker.coach, CoachDtoBuilder.makerWithLoginInfo().make()),
                    with(AppointmentDtoMaker.client, null as ClientDto?))
                .make()
                .toAppointment()
        }

        then(exception3).isInstanceOf(IllegalStateException::class.java)
        then(exception3).hasMessageContaining("client can not be null")

    }

}
