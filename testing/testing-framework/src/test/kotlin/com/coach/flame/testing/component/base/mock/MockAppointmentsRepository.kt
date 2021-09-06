package com.coach.flame.testing.component.base.mock

import com.coach.flame.jpa.entity.Appointment
import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.repository.AppointmentRepository
import com.coach.flame.jpa.repository.CoachRepository
import io.mockk.Answer
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.slot
import org.assertj.core.api.BDDAssertions.then
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

    fun mockFindAppointments(uuidCoach: UUID, uuidClient: UUID, listOfAppointments: List<Appointment>) {
        every { appointmentRepositoryMock.findAppointments(uuidCoach, uuidClient) } returns listOfAppointments
    }

    fun mockFindAppointmentsByCoachBetweenDate(
        uuidCoach: UUID,
        from: LocalDateTime,
        to: LocalDateTime,
        listOfAppointments: List<Appointment>,
    ) {
        every {
            appointmentRepositoryMock.findAppointmentsByCoachBetweenDates(uuidCoach, from, to)
        } returns listOfAppointments
    }

    fun mockFindAppointmentsByClient(client: Client, listOfAppointments: List<Appointment>) {
        every { appointmentRepositoryMock.findAppointmentsByClient(client.uuid) } returns listOfAppointments
    }

    fun mockFindAppointmentsByCoach(coach: Coach, listOfAppointments: List<Appointment>) {
        every { appointmentRepositoryMock.findAppointmentsByCoach(coach.uuid) } returns listOfAppointments
    }

    fun findByUuidAndDeleteFalse(uuidAppointment: UUID, appointment: Appointment?) {
        every { appointmentRepositoryMock.findByUuidAndDeleteFalse(uuidAppointment) } returns appointment
    }

}
