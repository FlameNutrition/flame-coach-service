package com.coach.flame.appointment.income

import com.coach.flame.date.DateHelper.toZonedDateTime
import com.coach.flame.domain.DateIntervalDto
import com.coach.flame.domain.maker.AppointmentDtoBuilder
import com.coach.flame.domain.maker.AppointmentDtoMaker
import com.coach.flame.domain.maker.IncomeDtoBuilder
import com.coach.flame.jpa.entity.Appointment.Companion.toAppointment
import com.coach.flame.jpa.repository.AppointmentRepository
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.util.*

@ExtendWith(MockKExtension::class)
class IncomeServiceImplTest {

    @MockK
    private lateinit var appointmentRepository: AppointmentRepository

    @InjectMockKs
    private lateinit var classToTest: IncomeServiceImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test get all accepted incomes`() {

        val from = LocalDate.of(2020, 1, 1)
        val to = LocalDate.of(2020, 12, 1)

        val uuid = UUID.randomUUID()
        val interval = DateIntervalDto(from, to)

        val listOfAppointments = listOf(
            AppointmentDtoBuilder.makerWithClientAndCoach()
                .but(with(AppointmentDtoMaker.dttmStarts, toZonedDateTime("2020-01-01T10:15:30+01:00")),
                    with(AppointmentDtoMaker.income, IncomeDtoBuilder.accepted()))
                .make()
                .toAppointment(),
            AppointmentDtoBuilder.makerWithClientAndCoach()
                .but(with(AppointmentDtoMaker.dttmStarts, toZonedDateTime("2021-01-03T10:15:30+01:00")),
                    with(AppointmentDtoMaker.income, IncomeDtoBuilder.accepted()))
                .make()
                .toAppointment(),
            AppointmentDtoBuilder.makerWithClientAndCoach()
                .but(with(AppointmentDtoMaker.dttmStarts, toZonedDateTime("2020-01-02T10:15:30+01:00")),
                    with(AppointmentDtoMaker.income, IncomeDtoBuilder.rejected()))
                .make()
                .toAppointment())

        every {
            appointmentRepository.getAppointmentsByCoachBetweenDates(
                uuid, from.atStartOfDay(), to.atStartOfDay()
            )
        } returns listOfAppointments

        val result = classToTest.getAcceptedIncomes(uuid, IncomeAggregator.Type.YEAR, interval)

        then(result).hasSize(1)

    }

}
