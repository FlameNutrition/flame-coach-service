package com.coach.flame.api.appointment

import com.coach.flame.api.appointment.request.AppointmentRequest
import com.coach.flame.appointment.AppointmentService
import com.coach.flame.domain.AppointmentDto
import com.coach.flame.domain.IncomeDto
import com.coach.flame.domain.maker.*
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.ZonedDateTime
import java.util.*

@ExtendWith(MockKExtension::class)
class AppointmentImplTest {

    @MockK
    private lateinit var appointmentService: AppointmentService

    @InjectMockKs
    private lateinit var classToTest: AppointmentImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test get appointments`() {

        val coachIdentifier = UUID.randomUUID()
        val clientIdentifier = UUID.randomUUID()

        val appointment1 = AppointmentDtoBuilder.maker()
            .but(with(AppointmentDtoMaker.identifier, UUID.randomUUID()),
                with(AppointmentDtoMaker.dttmStarts, ZonedDateTime.parse("2021-07-15T03:52:52.389929-04:00")),
                with(AppointmentDtoMaker.dttmEnds, ZonedDateTime.parse("2021-07-15T04:52:52.389929-04:00")),
                with(AppointmentDtoMaker.client, ClientDtoBuilder.default()),
                with(AppointmentDtoMaker.income, IncomeDtoBuilder.maker()
                    .but(with(IncomeDtoMaker.price, 200.5f))
                    .make()),
                with(AppointmentDtoMaker.notes, "Simple test"))
            .make()

        val appointment2 = AppointmentDtoBuilder.maker()
            .but(with(AppointmentDtoMaker.identifier, UUID.randomUUID()),
                with(AppointmentDtoMaker.dttmEnds, ZonedDateTime.parse("2021-07-15T03:52:52.389929-04:00")),
                with(AppointmentDtoMaker.dttmEnds, ZonedDateTime.parse("2021-07-15T04:52:52.389929-04:00")),
                with(AppointmentDtoMaker.client, ClientDtoBuilder.default()),
                with(AppointmentDtoMaker.income, IncomeDtoBuilder.maker()
                    .but(with(IncomeDtoMaker.price, 100.5f))
                    .make()),
                with(AppointmentDtoMaker.notes, "Simple test 2"))
            .make()

        every {
            appointmentService.getAppointments(coachIdentifier, clientIdentifier)
        } returns listOf(appointment1, appointment2)

        val response = classToTest.getAppointments(coachIdentifier, clientIdentifier)

        then(response.appointments).hasSize(2)

        val firstAppointment = response.appointments.first { it.identifier == appointment1.identifier.toString() }

        then(firstAppointment.price).isEqualTo(200.5f)
        then(firstAppointment.notes).isEqualTo("Simple test")
        then(firstAppointment.dttmStarts).isEqualTo("2021-07-15T03:52:52.389929-04:00")
        then(firstAppointment.dttmEnds).isEqualTo("2021-07-15T04:52:52.389929-04:00")
        then(firstAppointment.client).isNotNull
        then(firstAppointment.client?.identifier).isEqualTo(appointment1.client?.identifier.toString())
        then(firstAppointment.client?.firstName).isEqualTo(appointment1.client?.firstName)
        then(firstAppointment.client?.lastName).isEqualTo(appointment1.client?.lastName)

    }

    @Test
    fun `test get all coach appointments`() {

        val coachIdentifier = UUID.randomUUID()
        val clientIdentifier1 = UUID.randomUUID()
        val clientIdentifier2 = UUID.randomUUID()

        val appointment1 = AppointmentDtoBuilder.maker()
            .but(with(AppointmentDtoMaker.identifier, UUID.randomUUID()),
                with(AppointmentDtoMaker.client, ClientDtoBuilder.maker()
                    .but(with(ClientDtoMaker.identifier, clientIdentifier1))
                    .make()),
                with(AppointmentDtoMaker.income, IncomeDtoBuilder.maker()
                    .but(with(IncomeDtoMaker.price, 200.5f))
                    .make()),
                with(AppointmentDtoMaker.notes, "Simple test"))
            .make()

        val appointment2 = AppointmentDtoBuilder.maker()
            .but(with(AppointmentDtoMaker.identifier, UUID.randomUUID()),
                with(AppointmentDtoMaker.client, ClientDtoBuilder.maker()
                    .but(with(ClientDtoMaker.identifier, clientIdentifier2))
                    .make()),
                with(AppointmentDtoMaker.income, IncomeDtoBuilder.maker()
                    .but(with(IncomeDtoMaker.price, 100.5f))
                    .make()),
                with(AppointmentDtoMaker.notes, "Simple test 2"))
            .make()

        every {
            appointmentService.getAllCoachAppointments(coachIdentifier)
        } returns listOf(appointment1, appointment2)

        val response = classToTest.getAppointmentsCoach(coachIdentifier)

        then(response.appointments).hasSize(2)

        val firstAppointment = response.appointments.first { it.identifier == appointment1.identifier.toString() }
        val otherAppointment = response.appointments.first { it.identifier == appointment2.identifier.toString() }

        then(firstAppointment.price).isEqualTo(200.5f)
        then(firstAppointment.notes).isEqualTo("Simple test")
        then(firstAppointment.client).isNotNull
        then(firstAppointment.client?.identifier).isEqualTo(appointment1.client?.identifier.toString())
        then(firstAppointment.client?.firstName).isEqualTo(appointment1.client?.firstName)
        then(firstAppointment.client?.lastName).isEqualTo(appointment1.client?.lastName)

        then(otherAppointment.client).isNotNull
        then(otherAppointment.client?.identifier).isEqualTo(appointment2.client?.identifier.toString())
        then(otherAppointment.client?.firstName).isEqualTo(appointment2.client?.firstName)
        then(otherAppointment.client?.lastName).isEqualTo(appointment2.client?.lastName)

    }

    @Test
    fun `test get all client appointments`() {

        val clientIdentifier = UUID.randomUUID()

        val client = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.identifier, clientIdentifier))
            .make()

        val appointment1 = AppointmentDtoBuilder.maker()
            .but(with(AppointmentDtoMaker.identifier, UUID.randomUUID()),
                with(AppointmentDtoMaker.client, client),
                with(AppointmentDtoMaker.income, IncomeDtoBuilder.maker()
                    .but(with(IncomeDtoMaker.price, 200.5f))
                    .make()),
                with(AppointmentDtoMaker.notes, "Simple test"))
            .make()

        val appointment2 = AppointmentDtoBuilder.maker()
            .but(with(AppointmentDtoMaker.identifier, UUID.randomUUID()),
                with(AppointmentDtoMaker.client, client),
                with(AppointmentDtoMaker.income, IncomeDtoBuilder.maker()
                    .but(with(IncomeDtoMaker.price, 100.5f))
                    .make()),
                with(AppointmentDtoMaker.notes, "Simple test 2"))
            .make()

        every {
            appointmentService.getAllClientAppointments(clientIdentifier)
        } returns listOf(appointment1, appointment2)

        val response = classToTest.getAppointmentsClient(clientIdentifier)

        then(response.appointments).hasSize(2)

        val firstAppointment = response.appointments.first { it.identifier == appointment1.identifier.toString() }
        val otherAppointment = response.appointments.first { it.identifier == appointment2.identifier.toString() }

        then(firstAppointment.price).isEqualTo(200.5f)
        then(firstAppointment.notes).isEqualTo("Simple test")
        then(firstAppointment.client).isNotNull
        then(firstAppointment.client?.identifier).isEqualTo(appointment1.client?.identifier.toString())
        then(firstAppointment.client?.firstName).isEqualTo(appointment1.client?.firstName)
        then(firstAppointment.client?.lastName).isEqualTo(appointment1.client?.lastName)

        then(otherAppointment.client).isNotNull
        then(otherAppointment.client?.identifier).isEqualTo(appointment2.client?.identifier.toString())
        then(otherAppointment.client?.firstName).isEqualTo(appointment2.client?.firstName)
        then(otherAppointment.client?.lastName).isEqualTo(appointment2.client?.lastName)

    }

    @Test
    fun `test create appointment`() {

        val coachIdentifier = UUID.randomUUID()
        val clientIdentifier = UUID.randomUUID()

        val request = AppointmentRequest(
            dttmStarts = "2021-07-15T15:52:52.389929+08:00",
            dttmEnds = "2021-07-15T16:52:52.389929+08:00",
            price = 20.5f,
            notes = "Simple note",
        )

        val appointment1 = AppointmentDtoBuilder.maker()
            .but(with(AppointmentDtoMaker.identifier, UUID.randomUUID()),
                with(AppointmentDtoMaker.client, ClientDtoBuilder.default()),
                with(AppointmentDtoMaker.dttmStarts, ZonedDateTime.parse("2021-07-15T15:52:52.389929+08:00")),
                with(AppointmentDtoMaker.dttmEnds, ZonedDateTime.parse("2021-07-15T16:52:52.389929+08:00")),
                with(AppointmentDtoMaker.income, IncomeDtoBuilder.maker()
                    .but(with(IncomeDtoMaker.price, 20.5f))
                    .make()),
                with(AppointmentDtoMaker.notes, "Simple note"))
            .make()

        val appointmentSlot = slot<AppointmentDto>()

        every {
            appointmentService.createAppointment(coachIdentifier, clientIdentifier, capture(appointmentSlot))
        } returns appointment1

        val response = classToTest.createAppointment(coachIdentifier, clientIdentifier, request)

        then(response.appointments).hasSize(1)

        val firstAppointment = response.appointments.first { it.identifier == appointment1.identifier.toString() }

        then(firstAppointment.price).isEqualTo(20.5f)
        then(firstAppointment.notes).isEqualTo("Simple note")
        then(firstAppointment.dttmStarts).isEqualTo("2021-07-15T15:52:52.389929+08:00")
        then(firstAppointment.dttmEnds).isEqualTo("2021-07-15T16:52:52.389929+08:00")
        then(firstAppointment.client).isNotNull
        then(firstAppointment.client?.identifier).isEqualTo(appointment1.client?.identifier.toString())
        then(firstAppointment.client?.firstName).isEqualTo(appointment1.client?.firstName)
        then(firstAppointment.client?.lastName).isEqualTo(appointment1.client?.lastName)

        then(appointmentSlot.isCaptured).isTrue
        then(appointmentSlot.captured.identifier).isNotNull
        then(appointmentSlot.captured.delete).isFalse
        then(appointmentSlot.captured.income.price).isEqualTo(20.5f)
        then(appointmentSlot.captured.income.status).isEqualTo(IncomeDto.IncomeStatus.PENDING)
        then(appointmentSlot.captured.notes).isEqualTo("Simple note")
        then(appointmentSlot.captured.dttmStarts).isEqualTo(ZonedDateTime.parse("2021-07-15T15:52:52.389929+08:00"))
        then(appointmentSlot.captured.dttmEnds).isEqualTo(ZonedDateTime.parse("2021-07-15T16:52:52.389929+08:00"))

    }

    @Test
    fun `test update appointment`() {

        val uuidAppointment = UUID.randomUUID()

        val request = AppointmentRequest(
            dttmStarts = "2021-07-15T15:52:52.389929+08:00",
            dttmEnds = "2021-07-17T16:52:52.389929+08:00",
            price = 100.5f,
            notes = "Simple note updated",
        )

        val appointment1 = AppointmentDtoBuilder.maker()
            .but(with(AppointmentDtoMaker.identifier, uuidAppointment),
                with(AppointmentDtoMaker.dttmStarts, ZonedDateTime.parse("2021-07-15T15:52:52.389929+08:00")),
                with(AppointmentDtoMaker.dttmEnds, ZonedDateTime.parse("2021-07-17T16:52:52.389929+08:00")),
                with(AppointmentDtoMaker.income, IncomeDtoBuilder.maker()
                    .but(with(IncomeDtoMaker.price, 100.5f))
                    .make()),
                with(AppointmentDtoMaker.client, ClientDtoBuilder.default()),
                with(AppointmentDtoMaker.notes, "Simple note updated"))
            .make()

        val appointmentSlot = slot<AppointmentDto>()

        every {
            appointmentService.updateAppointment(capture(appointmentSlot))
        } returns appointment1

        val response = classToTest.updateAppointment(uuidAppointment, request)

        then(response.appointments).hasSize(1)

        val firstAppointment = response.appointments.first { it.identifier == appointment1.identifier.toString() }

        then(firstAppointment.price).isEqualTo(100.5f)
        then(firstAppointment.notes).isEqualTo("Simple note updated")
        then(firstAppointment.dttmStarts).isEqualTo("2021-07-15T15:52:52.389929+08:00")
        then(firstAppointment.dttmEnds).isEqualTo("2021-07-17T16:52:52.389929+08:00")
        then(firstAppointment.client).isNotNull
        then(firstAppointment.client?.identifier).isEqualTo(appointment1.client?.identifier.toString())
        then(firstAppointment.client?.firstName).isEqualTo(appointment1.client?.firstName)
        then(firstAppointment.client?.lastName).isEqualTo(appointment1.client?.lastName)

        then(appointmentSlot.isCaptured).isTrue
        then(appointmentSlot.captured.identifier).isNotNull
        then(appointmentSlot.captured.delete).isFalse
        then(appointmentSlot.captured.income.price).isEqualTo(100.5f)
        then(appointmentSlot.captured.income.status).isEqualTo(IncomeDto.IncomeStatus.PENDING)
        then(appointmentSlot.captured.notes).isEqualTo("Simple note updated")
        then(appointmentSlot.captured.dttmStarts).isEqualTo(ZonedDateTime.parse("2021-07-15T15:52:52.389929+08:00"))
        then(appointmentSlot.captured.dttmEnds).isEqualTo(ZonedDateTime.parse("2021-07-17T16:52:52.389929+08:00"))

    }

    @Test
    fun `test delete appointment`() {

        val uuidAppointment = UUID.randomUUID()

        every {
            appointmentService.deleteAppointment(uuidAppointment)
        } returns mockk()

        val response = classToTest.deleteAppointment(uuidAppointment)

        then(response.appointments).hasSize(1)

        val firstAppointment = response.appointments.first { it.identifier == uuidAppointment.toString() }

        then(firstAppointment.identifier).isEqualTo(uuidAppointment.toString())

    }

}
