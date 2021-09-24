package com.coach.flame.testing.component.base.mock

import com.coach.flame.jpa.entity.Appointment
import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.repository.AppointmentRepository
import io.mockk.CapturingSlot
import io.mockk.MockKStubScope
import io.mockk.every
import io.mockk.slot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestComponent
import java.time.LocalDateTime
import java.util.*

@TestComponent
class MockAppointmentsRepository : MockRepository<MockAppointmentsRepository, Appointment>() {
    companion object {
        const val GET_APPOINTMENT_BY_COACH_AND_DTTM_STARTS = "getAppointmentByCoachAndDttmStarts"
        const val GET_APPOINTMENT_BY_CLIENT_AND_DTTM_STARTS = "getAppointmentByClientAndDttmStarts"
        const val SAVE = "save"
        const val GET_APPOINTMENTS = "getAppointments"
        const val GET_APPOINTMENTS_BY_COACH_BETWEEN_DATES = "getAppointmentsByCoachBetweenDates"
        const val GET_APPOINTMENTS_BY_CLIENT_BETWEEN_DATES = "getAppointmentsByClientBetweenDate"
        const val GET_APPOINTMENTS_BY_CLIENT = "getAppointmentsByClient"
        const val GET_APPOINTMENTS_BY_COACH = "getAppointmentsByCoach"
        const val FIND_BY_UUID_AND_DELETE_FALSE = "findByUuidAndDeleteFalse"
    }

    @Autowired
    private lateinit var appointmentRepositoryMock: AppointmentRepository

    private fun save(): CapturingSlot<Appointment> {
        val slot = slot<Appointment>()
        every { appointmentRepositoryMock.save(capture(slot)) } answers { slot.captured }
        return slot
    }

    private fun getAppointments(coach: Coach, client: Client): MockKStubScope<Any, Any> {
        return every { appointmentRepositoryMock.getAppointments(coach.uuid, client.uuid) }
    }

    private fun getAppointmentsByCoachBetweenDate(
        coach: Coach,
        from: LocalDateTime,
        to: LocalDateTime
    ): MockKStubScope<Any, Any> {
        return every {
            appointmentRepositoryMock.getAppointmentsByCoachBetweenDates(coach.uuid, from, to)
        }
    }

    private fun getAppointmentsByClientBetweenDate(
        client: Client,
        from: LocalDateTime,
        to: LocalDateTime
    ): MockKStubScope<Any, Any> {
        return every {
            appointmentRepositoryMock.getAppointmentsByClientBetweenDates(client.uuid, from, to)
        }
    }

    private fun getAppointmentsByClient(client: Client): MockKStubScope<Any, Any> {
        return every { appointmentRepositoryMock.getAppointmentsByClient(client.uuid) }
    }

    private fun getAppointmentsByCoach(coach: Coach): MockKStubScope<Any, Any> {
        return every { appointmentRepositoryMock.getAppointmentsByCoach(coach.uuid) }
    }

    private fun findByUuidAndDeleteFalse(uuidAppointment: UUID): MockKStubScope<Any?, Any?> {
        return every { appointmentRepositoryMock.findByUuidAndDeleteFalse(uuidAppointment) }
    }

    private fun getAppointmentByCoachAndDttmStarts(coach: Coach): MockKStubScope<Any, Any> {
        return every {
            appointmentRepositoryMock.getAppointmentByCoachAndDttmStarts(
                coach.uuid,
                any()
            )
        }
    }

    private fun getAppointmentByClientAndDttmStarts(client: Client): MockKStubScope<Any, Any> {
        return every {
            appointmentRepositoryMock.getAppointmentByClientAndDttmStarts(
                client.uuid,
                any()
            )
        }
    }

    override fun returnsBool(f: () -> Boolean) {
        throw UnsupportedOperationException("returnsBool doest not have any method implemented!")
    }

    override fun returnsMulti(f: () -> List<Appointment?>) {
        when (mockMethod) {
            GET_APPOINTMENT_BY_COACH_AND_DTTM_STARTS ->
                getAppointmentByCoachAndDttmStarts(
                    (mockParams.getOrElse("coach") { throw RuntimeException("Missing coach param") } as Coach)
                ) returns f.invoke()
            GET_APPOINTMENT_BY_CLIENT_AND_DTTM_STARTS ->
                getAppointmentByClientAndDttmStarts(
                    (mockParams.getOrElse("client") { throw RuntimeException("Missing coach param") } as Client)
                ) returns f.invoke()
            GET_APPOINTMENTS ->
                getAppointments(
                    (mockParams.getOrElse("coach") { throw RuntimeException("Missing coach param") } as Coach),
                    (mockParams.getOrElse("client") { throw RuntimeException("Missing client param") } as Client)
                ) returns f.invoke()
            GET_APPOINTMENTS_BY_COACH_BETWEEN_DATES ->
                getAppointmentsByCoachBetweenDate(
                    (mockParams.getOrElse("coach") { throw RuntimeException("Missing coach param") } as Coach),
                    (mockParams.getOrElse("from") { throw RuntimeException("Missing from param") } as LocalDateTime),
                    (mockParams.getOrElse("to") { throw RuntimeException("Missing from to") } as LocalDateTime),
                ) returns f.invoke()
            GET_APPOINTMENTS_BY_CLIENT_BETWEEN_DATES ->
                getAppointmentsByClientBetweenDate(
                    (mockParams.getOrElse("client") { throw RuntimeException("Missing client param") } as Client),
                    (mockParams.getOrElse("from") { throw RuntimeException("Missing from param") } as LocalDateTime),
                    (mockParams.getOrElse("to") { throw RuntimeException("Missing from to") } as LocalDateTime),
                ) returns f.invoke()
            GET_APPOINTMENTS_BY_CLIENT ->
                getAppointmentsByClient(
                    (mockParams.getOrElse("client") { throw RuntimeException("Missing client param") } as Client),
                ) returns f.invoke()
            GET_APPOINTMENTS_BY_COACH ->
                getAppointmentsByCoach(
                    (mockParams.getOrElse("coach") { throw RuntimeException("Missing coach param") } as Coach),
                ) returns f.invoke()
            FIND_BY_UUID_AND_DELETE_FALSE ->
                findByUuidAndDeleteFalse(
                    (mockParams.getOrElse("uuid") { throw RuntimeException("Missing uuid param") } as UUID),
                ) returns f.invoke().first()
            else -> throw RuntimeException("Missing mock method name!")
        }

        clean()
    }

    override fun returns(f: () -> Appointment?) {
        val mockKStubScope: MockKStubScope<Any?, Any?> = when (mockMethod) {
            FIND_BY_UUID_AND_DELETE_FALSE ->
                findByUuidAndDeleteFalse(
                    (mockParams.getOrElse("uuid") { throw RuntimeException("Missing uuid param") } as UUID),
                )
            else -> throw RuntimeException("Missing mock method name!")
        }

        try {
            mockKStubScope returns f.invoke()
        } catch (ex: Exception) {
            mockKStubScope throws ex
        }

        clean()
    }

    override fun capture(): CapturingSlot<Appointment> {

        return when (mockMethod) {
            SAVE -> save()
            else -> throw RuntimeException("Missing mock method name!")
        }

    }

}
