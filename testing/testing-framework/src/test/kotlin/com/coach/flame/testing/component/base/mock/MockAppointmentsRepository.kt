package com.coach.flame.testing.component.base.mock

import com.coach.flame.jpa.entity.Appointment
import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.repository.AppointmentRepository
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.slot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestComponent
import java.time.LocalDateTime
import java.util.*

@TestComponent
class MockAppointmentsRepository {

    @Autowired
    private lateinit var appointmentRepositoryMock: AppointmentRepository

    fun captureSave(): CapturingSlot<Appointment> {
        val slot = slot<Appointment>()
        every { appointmentRepositoryMock.save(capture(slot)) } answers {
            slot.captured
        }
        return slot
    }

    fun mockGetAppointments(uuidCoach: UUID, uuidClient: UUID, listOfAppointments: List<Appointment>) {
        every { appointmentRepositoryMock.getAppointments(uuidCoach, uuidClient) } returns listOfAppointments
    }

    fun mockGetAppointmentsByCoachBetweenDate(
        coach: Coach,
        from: LocalDateTime,
        to: LocalDateTime,
        listOfAppointments: List<Appointment>,
    ) {
        every {
            appointmentRepositoryMock.getAppointmentsByCoachBetweenDates(coach.uuid, from, to)
        } returns listOfAppointments
    }

    fun mockGetAppointmentsByClientBetweenDate(
        client: Client,
        from: LocalDateTime,
        to: LocalDateTime,
        listOfAppointments: List<Appointment>,
    ) {
        every {
            appointmentRepositoryMock.getAppointmentsByClientBetweenDates(client.uuid, from, to)
        } returns listOfAppointments
    }

    fun mockGetAppointmentsByClient(client: Client, listOfAppointments: List<Appointment>) {
        every { appointmentRepositoryMock.getAppointmentsByClient(client.uuid) } returns listOfAppointments
    }

    fun mockGetAppointmentsByCoach(coach: Coach, listOfAppointments: List<Appointment>) {
        every { appointmentRepositoryMock.getAppointmentsByCoach(coach.uuid) } returns listOfAppointments
    }

    fun findByUuidAndDeleteFalse(uuidAppointment: UUID, appointment: Appointment?) {
        every { appointmentRepositoryMock.findByUuidAndDeleteFalse(uuidAppointment) } returns appointment
    }

    fun mockGetAppointmentByCoachAndDttmStarts(
        coach: Coach,
        listOfAppointments: List<Appointment>
    ) {
        every {
            appointmentRepositoryMock.getAppointmentByCoachAndDttmStarts(
                coach.uuid,
                any()
            )
        } returns listOfAppointments
    }

    fun mockGetAppointmentByClientAndDttmStarts(
        client: Client,
        listOfAppointments: List<Appointment>
    ) {
        every {
            appointmentRepositoryMock.getAppointmentByClientAndDttmStarts(
                client.uuid,
                any()
            )
        } returns listOfAppointments
    }

}
