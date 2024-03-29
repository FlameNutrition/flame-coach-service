package com.coach.flame.appointment

import com.coach.flame.domain.CustomerTypeDto
import com.coach.flame.domain.DateIntervalDto
import com.coach.flame.domain.IncomeDto
import com.coach.flame.domain.maker.AppointmentDtoBuilder
import com.coach.flame.domain.maker.AppointmentDtoMaker
import com.coach.flame.domain.maker.IncomeDtoBuilder
import com.coach.flame.domain.maker.IncomeDtoMaker
import com.coach.flame.failure.exception.CustomerNotFoundException
import com.coach.flame.jpa.entity.Appointment
import com.coach.flame.jpa.entity.Appointment.Companion.toAppointment
import com.coach.flame.jpa.entity.maker.ClientBuilder
import com.coach.flame.jpa.entity.maker.ClientMaker
import com.coach.flame.jpa.entity.maker.CoachBuilder
import com.coach.flame.jpa.entity.maker.CoachMaker
import com.coach.flame.jpa.repository.AppointmentRepository
import com.coach.flame.jpa.repository.operations.ClientRepositoryOperation
import com.coach.flame.jpa.repository.operations.CoachRepositoryOperation
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.*

@ExtendWith(MockKExtension::class)
class AppointmentServiceImplTest {

    @MockK
    private lateinit var coachOperations: CoachRepositoryOperation

    @MockK
    private lateinit var clientOperations: ClientRepositoryOperation

    @MockK
    private lateinit var appointmentRepository: AppointmentRepository

    @InjectMockKs
    private lateinit var appointmentServiceImpl: AppointmentServiceImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test get appointments without interval filter`() {

        val uuidCoach = UUID.randomUUID()
        val uuidClient = UUID.randomUUID()

        val client = ClientBuilder.maker()
            .but(with(ClientMaker.uuid, uuidClient))
            .make()
        val coach = CoachBuilder.maker()
            .but(
                with(CoachMaker.uuid, uuidCoach),
                with(CoachMaker.clients, mutableListOf(client))
            )
            .make()

        every {
            coachOperations.getCoach(any())
        } returns coach

        every {
            appointmentRepository.getAppointments(any(), any())
        } returns listOf(
            AppointmentDtoBuilder.maker()
                .but(
                    with(AppointmentDtoMaker.dttmStarts, ZonedDateTime.parse("2021-07-14T09:52:52.389929+01:00")),
                    with(AppointmentDtoMaker.coach, coach.toDto()),
                    with(AppointmentDtoMaker.client, client.toDto())
                )
                .make()
                .toAppointment()
        )

        val result = appointmentServiceImpl.getAppointments(uuidCoach, uuidClient, Optional.empty())

        verify { coachOperations.getCoach(uuidCoach) }
        verify { appointmentRepository.getAppointments(uuidCoach, uuidClient) }

        then(result).hasSize(1)
    }

    @Test
    fun `test get appointments with interval filter`() {

        val uuidCoach = UUID.randomUUID()
        val uuidClient = UUID.randomUUID()
        val from = LocalDate.of(2021, 1, 31)
        val to = LocalDate.of(2021, 12, 31)

        val client = ClientBuilder.maker()
            .but(with(ClientMaker.uuid, uuidClient))
            .make()
        val coach = CoachBuilder.maker()
            .but(
                with(CoachMaker.uuid, uuidCoach),
                with(CoachMaker.clients, mutableListOf(client))
            )
            .make()

        every {
            coachOperations.getCoach(any())
        } returns coach

        every {
            appointmentRepository.getAppointmentsBetweenDates(any(), any(), any(), any())
        } returns listOf(
            AppointmentDtoBuilder.maker()
                .but(
                    with(AppointmentDtoMaker.dttmStarts, ZonedDateTime.parse("2021-07-14T09:52:52.389929+01:00")),
                    with(AppointmentDtoMaker.coach, coach.toDto()),
                    with(AppointmentDtoMaker.client, client.toDto())
                )
                .make()
                .toAppointment()
        )

        val result = appointmentServiceImpl.getAppointments(
            uuidCoach, uuidClient,
            Optional.of(DateIntervalDto(from, to))
        )

        verify { coachOperations.getCoach(uuidCoach) }
        verify {
            appointmentRepository.getAppointmentsBetweenDates(
                uuidCoach,
                uuidClient,
                from.atStartOfDay(),
                to.plusDays(1).atStartOfDay()
            )
        }

        then(result).hasSize(1)
    }

    @Test
    fun `test get client appointments without interval filter`() {

        val uuidCoach = UUID.randomUUID()
        val uuidClient = UUID.randomUUID()

        val client = ClientBuilder.maker()
            .but(with(ClientMaker.uuid, uuidClient))
            .make()
        val coach = CoachBuilder.maker()
            .but(
                with(CoachMaker.uuid, uuidCoach),
                with(CoachMaker.clients, mutableListOf(client))
            )
            .make()

        every {
            clientOperations.getClient(any())
        } returns client

        every {
            appointmentRepository.getAppointmentsByClient(any())
        } returns listOf(
            AppointmentDtoBuilder.maker()
                .but(
                    with(AppointmentDtoMaker.dttmStarts, ZonedDateTime.parse("2021-07-14T09:52:52.389929+01:00")),
                    with(AppointmentDtoMaker.coach, coach.toDto()),
                    with(AppointmentDtoMaker.client, client.toDto())
                )
                .make()
                .toAppointment()
        )

        val result = appointmentServiceImpl.getAllClientAppointments(uuidClient, Optional.empty())

        verify { clientOperations.getClient(uuidClient) }
        verify { appointmentRepository.getAppointmentsByClient(client.uuid) }

        then(result).hasSize(1)
    }

    @Test
    fun `test get client appointments with interval filter`() {

        val uuidCoach = UUID.randomUUID()
        val uuidClient = UUID.randomUUID()
        val from = LocalDate.of(2021, 1, 31)
        val to = LocalDate.of(2021, 12, 31)

        val client = ClientBuilder.maker()
            .but(with(ClientMaker.uuid, uuidClient))
            .make()
        val coach = CoachBuilder.maker()
            .but(
                with(CoachMaker.uuid, uuidCoach),
                with(CoachMaker.clients, mutableListOf(client))
            )
            .make()

        every {
            clientOperations.getClient(any())
        } returns client

        every {
            appointmentRepository.getAppointmentsByClientBetweenDates(any(), any(), any())
        } returns listOf(
            AppointmentDtoBuilder.maker()
                .but(
                    with(AppointmentDtoMaker.dttmStarts, ZonedDateTime.parse("2021-07-14T09:52:52.389929+01:00")),
                    with(AppointmentDtoMaker.coach, coach.toDto()),
                    with(AppointmentDtoMaker.client, client.toDto())
                )
                .make()
                .toAppointment()
        )

        val result = appointmentServiceImpl.getAllClientAppointments(
            uuidClient,
            Optional.of(DateIntervalDto(from, to))
        )

        verify { clientOperations.getClient(uuidClient) }
        verify {
            appointmentRepository.getAppointmentsByClientBetweenDates(
                client.uuid,
                from.atStartOfDay(),
                to.plusDays(1).atStartOfDay()
            )
        }

        then(result).hasSize(1)
    }

    @Test
    fun `test get appointments customer not found`() {

        val uuidCoach = UUID.randomUUID()
        val uuidClient = UUID.randomUUID()

        val coach = CoachBuilder.maker()
            .but(with(CoachMaker.uuid, uuidCoach))
            .make()

        every {
            coachOperations.getCoach(any())
        } returns coach

        val exception =
            catchThrowable { appointmentServiceImpl.getAppointments(uuidCoach, uuidClient, Optional.empty()) }

        verify { coachOperations.getCoach(uuidCoach) }

        then(exception).isInstanceOf(CustomerNotFoundException::class.java)
        then(exception).hasMessageContaining("Could not find any client with uuid:")

    }

    @Test
    fun `test get coach appointments without interval filter`() {

        val uuidCoach = UUID.randomUUID()
        val uuidClient1 = UUID.randomUUID()
        val uuidClient2 = UUID.randomUUID()

        val client1 = ClientBuilder.maker()
            .but(with(ClientMaker.uuid, uuidClient1))
            .make()
        val client2 = ClientBuilder.maker()
            .but(with(ClientMaker.uuid, uuidClient2))
            .make()
        val coach = CoachBuilder.maker()
            .but(
                with(CoachMaker.uuid, uuidCoach),
                with(CoachMaker.clients, mutableListOf(client1, client2))
            )
            .make()

        every {
            coachOperations.getCoach(any())
        } returns coach

        every {
            appointmentRepository.getAppointmentsByCoach(any())
        } returns listOf(
            AppointmentDtoBuilder.maker()
                .but(
                    with(AppointmentDtoMaker.dttmStarts, ZonedDateTime.parse("2021-07-14T09:52:52.389929+01:00")),
                    with(AppointmentDtoMaker.coach, coach.toDto()),
                    with(AppointmentDtoMaker.client, client1.toDto())
                )
                .make()
                .toAppointment(),
            AppointmentDtoBuilder.maker()
                .but(
                    with(AppointmentDtoMaker.dttmStarts, ZonedDateTime.parse("2021-07-14T09:52:52.389929+01:00")),
                    with(AppointmentDtoMaker.coach, coach.toDto()),
                    with(AppointmentDtoMaker.client, client2.toDto())
                )
                .make()
                .toAppointment()
        )

        val result = appointmentServiceImpl.getAllCoachAppointments(uuidCoach, Optional.empty())

        verify { coachOperations.getCoach(uuidCoach) }
        verify { appointmentRepository.getAppointmentsByCoach(coach.uuid) }

        then(result).hasSize(2)

        val appointment = result.first()
        val other = result.last()

        then(appointment.client).isEqualTo(client1.toDto())
        then(other.client).isEqualTo(client2.toDto())

    }

    @Test
    fun `test get coach appointments with interval filter`() {

        val uuidCoach = UUID.randomUUID()
        val uuidClient1 = UUID.randomUUID()
        val uuidClient2 = UUID.randomUUID()
        val from = LocalDate.of(2021, 1, 31)
        val to = LocalDate.of(2021, 12, 31)

        val client1 = ClientBuilder.maker()
            .but(with(ClientMaker.uuid, uuidClient1))
            .make()
        val client2 = ClientBuilder.maker()
            .but(with(ClientMaker.uuid, uuidClient2))
            .make()
        val coach = CoachBuilder.maker()
            .but(
                with(CoachMaker.uuid, uuidCoach),
                with(CoachMaker.clients, mutableListOf(client1, client2))
            )
            .make()

        every {
            coachOperations.getCoach(any())
        } returns coach

        every {
            appointmentRepository.getAppointmentsByCoachBetweenDates(any(), any(), any())
        } returns listOf(
            AppointmentDtoBuilder.maker()
                .but(
                    with(AppointmentDtoMaker.dttmStarts, ZonedDateTime.parse("2021-07-14T09:52:52.389929+01:00")),
                    with(AppointmentDtoMaker.coach, coach.toDto()),
                    with(AppointmentDtoMaker.client, client1.toDto())
                )
                .make()
                .toAppointment(),
            AppointmentDtoBuilder.maker()
                .but(
                    with(AppointmentDtoMaker.dttmStarts, ZonedDateTime.parse("2021-07-14T09:52:52.389929+01:00")),
                    with(AppointmentDtoMaker.coach, coach.toDto()),
                    with(AppointmentDtoMaker.client, client2.toDto())
                )
                .make()
                .toAppointment()
        )

        val result = appointmentServiceImpl.getAllCoachAppointments(
            uuidCoach,
            Optional.of(DateIntervalDto(from, to))
        )

        verify { coachOperations.getCoach(uuidCoach) }
        verify {
            appointmentRepository.getAppointmentsByCoachBetweenDates(
                coach.uuid,
                from.atStartOfDay(),
                to.plusDays(1).atStartOfDay()
            )
        }

        then(result).hasSize(2)

        val appointment = result.first()
        val other = result.last()

        then(appointment.client).isEqualTo(client1.toDto())
        then(other.client).isEqualTo(client2.toDto())

    }

    @Test
    fun `test create new appointment`() {

        val uuidCoach = UUID.randomUUID()
        val uuidClient = UUID.randomUUID()

        val client = ClientBuilder.maker()
            .but(with(ClientMaker.uuid, uuidClient))
            .make()

        val coach = CoachBuilder.maker()
            .but(
                with(CoachMaker.uuid, uuidCoach),
                with(CoachMaker.clients, mutableListOf(client))
            )
            .make()

        val appointmentInjected = slot<Appointment>()

        every { coachOperations.getCoach(any()) } returns coach
        every { appointmentRepository.save(capture(appointmentInjected)) } answers { appointmentInjected.captured }

        appointmentServiceImpl
            .createAppointment(
                uuidCoach, uuidClient, AppointmentDtoBuilder
                    .maker()
                    .but(with(AppointmentDtoMaker.dttmStarts, ZonedDateTime.parse("2021-07-14T16:52:52.389929+08:00")))
                    .make()
            )

        verify { coachOperations.getCoach(uuidCoach) }

        then(appointmentInjected.isCaptured).isTrue
        then(appointmentInjected.captured.coach).isNotNull
        then(appointmentInjected.captured.client).isNotNull
        then(appointmentInjected.captured.dttmStarts).isEqualTo(LocalDateTime.parse("2021-07-14T09:52:52.389929"))

    }

    @Test
    fun `test create new appointment customer not found`() {

        val uuidCoach = UUID.randomUUID()
        val uuidClient = UUID.randomUUID()

        val coach = CoachBuilder.maker()
            .but(with(CoachMaker.uuid, uuidCoach))
            .make()

        every { coachOperations.getCoach(any()) } returns coach

        val exception = catchThrowable {
            appointmentServiceImpl
                .createAppointment(
                    uuidCoach, uuidClient, AppointmentDtoBuilder
                        .maker()
                        .but(
                            with(
                                AppointmentDtoMaker.dttmStarts,
                                ZonedDateTime.parse("2021-07-14T16:52:52.389929+08:00")
                            )
                        )
                        .make()
                )
        }

        verify { coachOperations.getCoach(uuidCoach) }

        then(exception).isInstanceOf(CustomerNotFoundException::class.java)
        then(exception).hasMessageContaining("Could not find any client with uuid:")

    }

    @Test
    fun `test update appointment`() {

        val appointmentIdentifier = UUID.randomUUID()

        val income = IncomeDtoBuilder.maker()
            .but(
                with(IncomeDtoMaker.price, 200.5f),
                with(IncomeDtoMaker.status, IncomeDto.IncomeStatus.REJECTED)
            )
            .make()

        val appointment = AppointmentDtoBuilder.maker()
            .but(
                with(AppointmentDtoMaker.identifier, appointmentIdentifier),
                with(AppointmentDtoMaker.income, income),
                with(AppointmentDtoMaker.delete, true),
                with(AppointmentDtoMaker.notes, "Hey hello!"),
                with(AppointmentDtoMaker.dttmStarts, ZonedDateTime.parse("2021-06-14T16:52:52.389929+09:00")),
                with(AppointmentDtoMaker.dttmEnds, ZonedDateTime.parse("2021-06-14T12:52:52.389929+09:00")),
                with(AppointmentDtoMaker.currency, Currency.getInstance("EUR"))
            )
            .make()

        val appointmentToUpdate = AppointmentDtoBuilder.maker()
            .but(
                with(AppointmentDtoMaker.id, 100L),
                with(AppointmentDtoMaker.identifier, appointmentIdentifier),
                with(AppointmentDtoMaker.coach, CoachBuilder.default().toDto()),
                with(AppointmentDtoMaker.client, ClientBuilder.default().toDto()),
                with(
                    AppointmentDtoMaker.income, IncomeDtoBuilder.maker()
                        .but(
                            with(IncomeDtoMaker.price, 500.5f),
                            with(IncomeDtoMaker.status, IncomeDto.IncomeStatus.ACCEPTED)
                        )
                        .make()
                )
            )
            .make()
            .toAppointment()

        val appointmentInjected = slot<Appointment>()

        every { appointmentRepository.findByUuidAndDeleteFalse(appointmentIdentifier) } returns appointmentToUpdate
        every { appointmentRepository.save(capture(appointmentInjected)) } answers { appointmentInjected.captured }

        appointmentServiceImpl.updateAppointment(appointment)

        then(appointmentInjected.isCaptured).isTrue
        then(appointmentInjected.captured.id).isEqualTo(100L)
        then(appointmentInjected.captured.uuid).isEqualTo(appointmentIdentifier)
        then(appointmentInjected.captured.coach).isNotNull
        then(appointmentInjected.captured.client).isNotNull
        then(appointmentInjected.captured.income).isNotNull
        then(appointmentInjected.captured.income.price).isEqualTo(200.5f)
        then(appointmentInjected.captured.income.status).isEqualTo("REJECTED")
        then(appointmentInjected.captured.dttmStarts).isEqualTo(LocalDateTime.parse("2021-06-14T08:52:52.389929"))
        then(appointmentInjected.captured.dttmEnds).isEqualTo(LocalDateTime.parse("2021-06-14T04:52:52.389929"))
        then(appointmentInjected.captured.delete).isFalse
        then(appointmentInjected.captured.currency).isEqualTo("EUR")
        then(appointmentInjected.captured.notes).isEqualTo("Hey hello!")

    }

    @Test
    fun `test update appointment but appointment does not exist`() {

        val appointmentIdentifier = UUID.randomUUID()

        val income = IncomeDtoBuilder.maker()
            .but(with(IncomeDtoMaker.price, 200.5f))
            .make()

        val appointment = AppointmentDtoBuilder.maker()
            .but(
                with(AppointmentDtoMaker.identifier, appointmentIdentifier),
                with(AppointmentDtoMaker.income, income),
                with(AppointmentDtoMaker.delete, true),
                with(AppointmentDtoMaker.notes, "Hey hello!"),
                with(AppointmentDtoMaker.dttmStarts, ZonedDateTime.parse("2021-06-14T16:52:52.389929+09:00")),
                with(AppointmentDtoMaker.currency, Currency.getInstance("EUR"))
            )
            .make()

        every { appointmentRepository.findByUuidAndDeleteFalse(appointmentIdentifier) } returns null

        val result = catchThrowable { appointmentServiceImpl.updateAppointment(appointment) }

        then(result).isInstanceOf(AppointmentNotFoundException::class.java)
        then(result).hasMessageContaining("Appointment not found, please check the identifier.")

    }

    @Test
    fun `test delete appointment`() {

        val appointmentIdentifier = UUID.randomUUID()

        val appointmentToDelete = AppointmentDtoBuilder.maker()
            .but(
                with(AppointmentDtoMaker.id, 100L),
                with(AppointmentDtoMaker.identifier, appointmentIdentifier),
                with(AppointmentDtoMaker.coach, CoachBuilder.default().toDto()),
                with(AppointmentDtoMaker.client, ClientBuilder.default().toDto())
            )
            .make()
            .toAppointment()

        val appointmentInjected = slot<Appointment>()

        every { appointmentRepository.findByUuidAndDeleteFalse(appointmentIdentifier) } returns appointmentToDelete
        every { appointmentRepository.save(capture(appointmentInjected)) } returns mockk()

        appointmentServiceImpl.deleteAppointment(appointmentIdentifier)

        then(appointmentInjected.isCaptured).isTrue
        then(appointmentInjected.captured.delete).isTrue

    }

    @Test
    fun `test delete appointment failure`() {

        val appointmentIdentifier = UUID.randomUUID()

        every { appointmentRepository.findByUuidAndDeleteFalse(appointmentIdentifier) } returns null

        val result = catchThrowable { appointmentServiceImpl.deleteAppointment(appointmentIdentifier) }

        verify { appointmentRepository.findByUuidAndDeleteFalse(appointmentIdentifier) }

        then(result).isInstanceOf(AppointmentMissingDeleteException::class.java)
        then(result).hasMessageContaining("Didn't find the following uuid appointment:")

    }

    @Test
    fun `test get next client appointment`() {

        val clientIdentifier = UUID.randomUUID()

        every { appointmentRepository.getAppointmentByClientAndDttmStarts(any(), any()) } returns listOf(
            AppointmentDtoBuilder
                .makerWithClientAndCoach()
                .make()
                .toAppointment()
        )

        appointmentServiceImpl.getNextAppointment(clientIdentifier, CustomerTypeDto.CLIENT)

        verify { appointmentRepository.getAppointmentByClientAndDttmStarts(clientIdentifier, isNull(inverse = true)) }

    }

    @Test
    fun `test get next coach appointment`() {

        val coachIdentifier = UUID.randomUUID()

        every { appointmentRepository.getAppointmentByCoachAndDttmStarts(any(), any()) } returns listOf(
            AppointmentDtoBuilder
                .makerWithClientAndCoach()
                .make()
                .toAppointment()
        )

        appointmentServiceImpl.getNextAppointment(coachIdentifier, CustomerTypeDto.COACH)

        verify { appointmentRepository.getAppointmentByCoachAndDttmStarts(coachIdentifier, isNull(inverse = true)) }

    }

    @Test
    fun `test get next coach or client appointment but list is empty`() {

        val uuid = UUID.randomUUID()

        val result = catchThrowable { appointmentServiceImpl.getNextAppointment(uuid, CustomerTypeDto.UNKNOWN) }

        verify(exactly = 0) { appointmentRepository.getAppointmentByCoachAndDttmStarts(any(), any()) }
        verify(exactly = 0) { appointmentRepository.getAppointmentByClientAndDttmStarts(any(), any()) }

        then(result).isInstanceOf(AppointmentNotFoundException::class.java)
        then(result).hasMessageContaining("Appointment not found.")

    }

}
