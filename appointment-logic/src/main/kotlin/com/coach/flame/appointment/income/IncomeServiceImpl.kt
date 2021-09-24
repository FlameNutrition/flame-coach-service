package com.coach.flame.appointment.income

import com.coach.flame.domain.DateIntervalDto
import com.coach.flame.domain.IncomeDto
import com.coach.flame.jpa.repository.AppointmentRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class IncomeServiceImpl(
    private val appointmentRepository: AppointmentRepository,
) : IncomeService {

    override fun getAcceptedIncomes(
        coachIdentifier: UUID,
        aggregatorType: IncomeAggregator.Type,
        interval: DateIntervalDto,
    ): Map<LocalDate, List<IncomeDto>> {

        val appointments = appointmentRepository.getAppointmentsByCoachBetweenDates(
            coachIdentifier, interval.from.atStartOfDay(), interval.to.atStartOfDay())

        val incomeGroupByDate: Map<LocalDate, List<IncomeDto>> = appointments
            .groupByTo(HashMap(), { it.dttmStarts.toLocalDate() }, { it.income.toDto() })

        return IncomeAggregator(interval, incomeGroupByDate).getIncomes(aggregatorType,
            Optional.of(IncomeAggregator.FILTER_ACCEPTED))
    }

}
